package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String email;
    private int idEmpleado;
    private String role;
    private String nombre;
    private String apellidos;
}
