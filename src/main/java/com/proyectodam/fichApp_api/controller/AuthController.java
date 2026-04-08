package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.ActivarCuentaDTO;
import com.proyectodam.fichApp_api.dto.LoginRequestDTO;
import com.proyectodam.fichApp_api.dto.LoginResponseDTO;
import com.proyectodam.fichApp_api.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService iAuthService;

    public AuthController(IAuthService iAuthService) {
        this.iAuthService = iAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginResponseDTO (@RequestBody LoginRequestDTO loginRequestDTO) {

        try {
            LoginResponseDTO loginResponseDTO = iAuthService.login(loginRequestDTO);
            return ResponseEntity.ok(loginResponseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<LoginResponseDTO> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) {
        return ResponseEntity.ok(iAuthService.activarCuenta(activarCuentaDTO));
    }
}