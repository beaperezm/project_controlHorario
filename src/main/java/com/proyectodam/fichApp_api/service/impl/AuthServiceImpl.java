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
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación local de autenticación para los perfiles local, server y remoteseed.
 * Valida credenciales contra la base de datos local usando BCrypt.
 * Devuelve LoginResponseDTO con tokens vacíos (no se usa JWT en estos perfiles).
 */
@Service
@Profile({"local", "server", "remoteseed"})
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Empleado empleado = empleadoRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (empleado.getPasswordHash() == null || empleado.getPasswordHash().isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), empleado.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
        String rolNombre = (contrato != null && contrato.getRol() != null) ? contrato.getRol().getNombre() : "USER";

        return new LoginResponseDTO("local-access-token-" + empleado.getIdEmpleado(),
                "local-refresh-token-" + empleado.getIdEmpleado(),
                empleado.getEmail(), empleado.getIdEmpleado(),
                rolNombre, empleado.getNombre(), empleado.getApellidos());
    }

    @Override
    public LoginResponseDTO registroEmpleado(RegisterRequestDTO registerRequestDTO) {
        throw new UnsupportedOperationException("El registro de empleados vía API solo está disponible en el perfil supabase");
    }

    @Override
    public LoginResponseDTO activarCuenta(ActivarCuentaDTO activarCuentaDTO) {
        throw new UnsupportedOperationException("La activación de cuenta requiere el modo Supabase. Cambie el modo de conexión a Supabase en Configuración y reinicie la aplicación.");
    }
}
