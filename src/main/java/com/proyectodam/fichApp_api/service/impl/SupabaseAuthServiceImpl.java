package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.ActivarCuentaDTO;
import com.proyectodam.fichApp_api.dto.LoginRequestDTO;
import com.proyectodam.fichApp_api.dto.LoginResponseDTO;
import com.proyectodam.fichApp_api.dto.RegisterRequestDTO;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.ContratoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.service.IAuthService;
import com.proyectodam.fichApp_api.service.IConfiguracionConexionService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.springframework.core.ParameterizedTypeReference;
import java.util.Map;

/**
 * Implementación Supabase de autenticación para el perfil supabase.
 * Delega login, registro y activación de cuenta en el servicio de auth de Supabase.
 */
@Service
@Profile("supabase")
public class SupabaseAuthServiceImpl implements IAuthService {

    @Value("${supabase.rest-url:}")
    private String supabaseUrl;

    @Value("${supabase.rest-key:}")
    private String annonKey;

    private final WebClient webClient;

    private final IConfiguracionConexionService configuracionService;

    private final EmpleadoRepository empleadoRepository;

    private final ContratoRepository contratoRepository;

    public SupabaseAuthServiceImpl(WebClient.Builder webClient, 
                                    IConfiguracionConexionService configuracionService,
                                    EmpleadoRepository empleadoRepository,
                                    ContratoRepository contratoRepository) {
        this.webClient = webClient.build();
        this.configuracionService = configuracionService;
        this.empleadoRepository = empleadoRepository;
        this.contratoRepository = contratoRepository;
    }

    private String getEffectiveUrl() {
        com.proyectodam.fichApp_api.model.ConfiguracionConexion config = configuracionService.obtenerConfiguracion();
        String url = null;
        if (config != null && config.getSupaUrl() != null && !config.getSupaUrl().trim().isEmpty()) {
            url = config.getSupaUrl().trim();
        } else {
            url = supabaseUrl;
        }

        if (url == null || url.trim().isEmpty()) {
            System.err.println("ERROR: La URL de Supabase no está configurada (ni en BD ni en variables de entorno).");
        }
        return url;
    }

    private String getEffectiveKey() {
        com.proyectodam.fichApp_api.model.ConfiguracionConexion config = configuracionService.obtenerConfiguracion();
        String key = null;
        if (config != null && config.getSupaKey() != null && !config.getSupaKey().trim().isEmpty()) {
            key = config.getSupaKey().trim();
        } else {
            key = annonKey;
        }

        if (key == null || key.trim().isEmpty()) {
            System.err.println("ERROR: La clave Anon de Supabase no está configurada.");
        }
        return key;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String effectiveUrl = getEffectiveUrl();
        String effectiveKey = getEffectiveKey();

        if (effectiveUrl == null || effectiveUrl.isEmpty()) {
            throw new RuntimeException("La URL de Supabase no está configurada. Por favor, revise la configuración del servidor.");
        }

        String url = effectiveUrl + "/auth/v1/token?grant_type=password";

        Map<String, String> body = Map.of(
                "email", loginRequestDTO.getEmail(),
                "password", loginRequestDTO.getPassword()
        );

        Map<String, Object> response = null;
        try {
            response = webClient.post()
                    .uri(url)
                    .header("apikey", effectiveKey)
                    .header("Authorization", "Bearer " + effectiveKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Error en login con Supabase: " + e.getMessage());
            if (e.getMessage().contains("Host is not specified")) {
                throw new RuntimeException("Error de configuración: La URL de Supabase '" + effectiveUrl + "' no es válida.");
            }
            throw new RuntimeException("Error de comunicación con Supabase: " + e.getMessage());
        }

        if (response == null) {
            throw new RuntimeException("Error autenticando con Supabase: Sin respuesta");
        }

        String accessToken = (String) response.get("access_token");
        String refreshToken = (String) response.get("refresh_token");

        String email;
        String userId;

        if (response.get("user") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) response.get("user");
            email = (String) user.get("email");
            userId = (String) user.get("id");
        } else {
            email = (String) response.get("email");
            userId = (String) response.get("id");
        }

        if (userId == null || email == null) {
            throw new RuntimeException("Respuesta inválida de Supabase: " + response);
        }

        Empleado empleado = empleadoRepository.findByAuthUserId(userId)
                .orElseGet(() -> empleadoRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Empleado no registrado")));

        if (!empleado.isActivoEnAuth()) {
            throw new RuntimeException("Cuenta no activada");
        }

        Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
        String rolNombre = (contrato != null && contrato.getRol() != null)
                ? contrato.getRol().getNombre() : "Usuario";

        return new LoginResponseDTO(accessToken, refreshToken, empleado.getEmail(),
                empleado.getIdEmpleado(), rolNombre, empleado.getNombre(), empleado.getApellidos());
    }

    @Override
    public LoginResponseDTO registroEmpleado(RegisterRequestDTO registerRequestDTO) {
        String effectiveUrl = getEffectiveUrl();
        String effectiveKey = getEffectiveKey();
        
        if (effectiveUrl == null || effectiveUrl.isEmpty()) {
            throw new RuntimeException("No se puede registrar: URL de Supabase no configurada.");
        }

        String url = effectiveUrl + "/auth/v1/signup";

        Map<String, Object> body = Map.of(
                "email", registerRequestDTO.getEmail(),
                "password", registerRequestDTO.getPassword()
        );

        Map<String, Object> response = webClient.post().uri(url)
                .header("apikey", effectiveKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null) {
            throw new RuntimeException("Error registrando usuario en Supabase");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) response.get("user");
        String userId = (String) user.get("id");

        Empleado empleado = new Empleado();
        empleado.setEmail(registerRequestDTO.getEmail());
        empleado.setNombre(registerRequestDTO.getNombre());
        empleado.setApellidos(registerRequestDTO.getApellidos());
        empleado.setAuthUserId(userId);

        empleadoRepository.save(empleado);

        return login(new LoginRequestDTO(registerRequestDTO.getEmail(), registerRequestDTO.getPassword()));
    }

    @Override
    public LoginResponseDTO activarCuenta(ActivarCuentaDTO activarCuentaDTO) {
        System.out.println("Iniciando proceso de activación para: " + activarCuentaDTO.getEmail());
        
        Empleado empleado = empleadoRepository.findByEmail(activarCuentaDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("El empleado no existe en la base de datos local."));

        String effectiveUrl = getEffectiveUrl();
        String effectiveKey = getEffectiveKey();

        if (effectiveUrl == null || effectiveUrl.isEmpty()) {
            throw new RuntimeException("Error de configuración: La URL de Supabase está vacía. Configure el servidor correctamente.");
        }

        String url = effectiveUrl + "/auth/v1/signup";
        
        Map<String, Object> body = Map.of(
                "email", activarCuentaDTO.getEmail(),
                "password", activarCuentaDTO.getPassword()
        );

        Map<String, Object> response = null;
        try {
            System.out.println("Conectando a Supabase: " + url);
            response = webClient.post().uri(url)
                    .header("apikey", effectiveKey)
                    .header("Authorization", "Bearer " + effectiveKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            System.out.println("Supabase respondió con error: " + errorBody);
            
            // Si el error es que ya existe en Supabase Auth, intentamos simplemente loguear
            if (errorBody.contains("User already registered") || errorBody.contains("already exists")) {
                System.out.println("El usuario ya existe en Supabase Auth. Intentando sincronizar y loguear...");
                return login(new LoginRequestDTO(activarCuentaDTO.getEmail(), activarCuentaDTO.getPassword()));
            }
            throw new RuntimeException("Error en Supabase Auth: " + errorBody);
        } catch (Exception e) {
            System.err.println("Error de comunicación: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("Host is not specified")) {
                throw new RuntimeException("URL de Supabase inválida o no especificada. Verifique la configuración.");
            }
            throw new RuntimeException("Error de comunicación: " + (e.getMessage() != null ? e.getMessage() : "Desconocido"));
        }


        if (response == null) {
            throw new RuntimeException("Error creando usuario en Supabase");
        }

        String userId;
        if (response.get("user") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) response.get("user");
            userId = (String) user.get("id");
        } else if (response.get("id") != null) {
            userId = (String) response.get("id");
        } else {
            throw new RuntimeException("No se pudo obtener userID de Supabase: " + response);
        }

        empleado.setAuthUserId(userId);
        empleado.setActivoEnAuth(true);
        empleadoRepository.save(empleado);

        return login(new LoginRequestDTO(activarCuentaDTO.getEmail(), activarCuentaDTO.getPassword()));
    }
}
