package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoEstadoContadorDTO {

    private long totalEmpleados;
    private long activos;
    private long inactivos;
    private long bajaMedica;
    private long excedencia;
}
