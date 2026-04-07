package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContadorDTO {

    private int idEmpleado;
    private long segundosTranscurridos; //tiempo trabajado en segundos
}
