package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDTO {

    private int idEmpleado;
    private String nombre;
    private String apellidos;
    private String email;
    private String direccion;
    private String telefono;
    private String dni;
    private String departamento;
    private String rol;
    private LocalDate fechaAlta;
    private LocalDate fechaNacimiento;
    private String estado;
}
