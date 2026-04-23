package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.ActivarCuentaDTO;
import com.proyectodam.fichApp_api.dto.LoginRequestDTO;
import com.proyectodam.fichApp_api.dto.LoginResponseDTO;
import com.proyectodam.fichApp_api.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService iAuthService;

    public AuthController(IAuthService iAuthService) {
        this.iAuthService = iAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            return ResponseEntity.ok(iAuthService.login(loginRequestDTO));
        } catch (RuntimeException e) {
            // Devolver un código diferente si la cuenta no está activada
            if (e.getMessage() != null && e.getMessage().contains("no activada")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<?> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) {
        try {
            return ResponseEntity.ok(iAuthService.activarCuenta(activarCuentaDTO));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Error al activar cuenta"));
        }
    }
}
