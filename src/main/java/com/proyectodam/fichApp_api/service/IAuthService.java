package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.ActivarCuentaDTO;
import com.proyectodam.fichApp_api.dto.LoginRequestDTO;
import com.proyectodam.fichApp_api.dto.LoginResponseDTO;
import com.proyectodam.fichApp_api.dto.RegisterRequestDTO;

public interface IAuthService {

    LoginResponseDTO login (LoginRequestDTO loginRequestDTO);
    LoginResponseDTO registroEmpleado(RegisterRequestDTO registerRequestDTO);
    LoginResponseDTO activarCuenta(ActivarCuentaDTO activarCuentaDTO);
}