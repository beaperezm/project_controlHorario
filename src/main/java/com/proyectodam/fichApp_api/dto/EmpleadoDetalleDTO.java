package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDetalleDTO {

    private int idEmpleado;
    private String nombreApellidos;
    private String email;
    private String telefono;
    private String direccion;
    private String departamento;
    private String rol;
    private int diasVacacionesTotales;
    private int diasVacacionesPendientes;
    private double horasExtra;
    private String horario;
    private String configuracionHorario;

}
