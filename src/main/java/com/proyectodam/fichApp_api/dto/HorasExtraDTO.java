package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorasExtraDTO {
    private double horasTrabajadas;
    private double horasContrato;
    private double horasExtra;
    private double saldoHoras;

}
