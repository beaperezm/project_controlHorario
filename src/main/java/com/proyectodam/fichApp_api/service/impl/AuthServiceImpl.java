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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class AuthServiceImpl implements IAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String annonKey;

    private final WebClient webClient;

    @Autowired
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    private final ContratoRepository contratoRepository;

    public AuthServiceImpl(WebClient.Builder webClient, EmpleadoRepository empleadoRepository, ContratoRepository contratoRepository) {
        this.webClient = webClient.build();
        this.empleadoRepository = empleadoRepository;
        this.contratoRepository = contratoRepository;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";


        Map<String, String> body = Map.of(
                "email", loginRequestDTO.getEmail(),
                "password", loginRequestDTO.getPassword()
        );

        Map response = webClient.post()
                .uri(url)
                .header("apikey", annonKey)
                .header("Authorization", "Bearer " + annonKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Error autenticando con Supabase");
        }

        String accessToken = (String) response.get("access_token");
        String refreshToken = (String) response.get("refresh_token");


        String email;
        String userId = null;
        
        if(response.get("user") != null) {
            Map user = (Map) response.get("user");
            email = (String) user.get("email");
            userId = (String) user.get("id");
        } else {
            email = (String) response.get("email");
            userId = (String) response.get("id");
        }

        if(userId == null || email == null) {
            throw new RuntimeException("Respuesta inválida de Supabase " + response);

        }

     //   Empleado empleado = empleadoRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Empleado no registrado"));
        Empleado empleado = empleadoRepository.findByAuthUserId(userId).orElseGet(() -> empleadoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Empleado no registrado")));

        if(!empleado.isActivoEnAuth()) {
            throw new RuntimeException("Cuenta no activada");
        }

        Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
        String rolNombre = (contrato != null && contrato.getRol() !=null) ? contrato.getRol().getNombre() : "Usuario";

        return new LoginResponseDTO(accessToken, refreshToken, empleado.getEmail(), empleado.getIdEmpleado(), rolNombre, empleado.getNombre(), empleado.getApellidos());

    }

    @Override
    public LoginResponseDTO registroEmpleado(RegisterRequestDTO registerRequestDTO) {
        String url = supabaseUrl + "/auth/v1/signup";

        Map<String, Object> body = Map.of("email", registerRequestDTO.getEmail(),
                "password", registerRequestDTO.getPassword());

        Map response = webClient.post().uri(url)
                .header("apikey", annonKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if(response == null) {
            throw new RuntimeException("Error registrando usuario");
        }

        Map user = (Map) response.get("user");
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

        Empleado empleado = empleadoRepository.findByEmail(activarCuentaDTO.getEmail()).orElseThrow(() -> new RuntimeException("El empleado no existe"));

        if(empleado.isActivoEnAuth()) {
            throw new RuntimeException("Cuenta ya registrada");
        }

        String url = supabaseUrl + "/auth/v1/signup";


        Map<String, Object> body = Map.of("email", activarCuentaDTO.getEmail(), "password", activarCuentaDTO.getPassword());

        Map response;
        try {
            response = webClient.post().uri(url)
                    .header("apikey", annonKey)
                    .header("Authorization", "Bearer " + annonKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();


        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error creando usuario: " + e.getResponseBodyAsString());
        }



        if (response == null) {
            throw new RuntimeException("Error creando usuario en Supabase " + response);
        }
        
        String userId = null;
        if(response.get("user") != null) {
            Map user = (Map) response.get("user");
            userId = (String) user.get("id");
        } else if(response.get("id") != null) {
            userId = (String) response.get("id");
        } else throw new RuntimeException("No se pudo obtener userID de Supabase " + response);


        empleado.setAuthUserId(userId);
        empleado.setActivoEnAuth(true);
        empleadoRepository.save(empleado);

        return login(new LoginRequestDTO(activarCuentaDTO.getEmail(), activarCuentaDTO.getPassword()));

    }
}