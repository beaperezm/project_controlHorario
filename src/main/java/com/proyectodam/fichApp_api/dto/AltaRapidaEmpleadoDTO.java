package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AltaRapidaEmpleadoDTO {

    private String nombre;
    private String apellidos;
    private String email;
    private String direccion;
    private String telefono;
    private String dni;
    private int idDepartamento;
    private int idRol;
    private LocalDate fechaAlta;
    private LocalDate fechaNacimiento;
    private int idEmpresa;
}
