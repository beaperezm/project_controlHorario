package com.proyectodam.fichApp_api.dto;

import com.proyectodam.fichApp_api.enums.MetodoFichaje;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FichajeRequestDTO {

    private int idEmpleado;
    private TipoEventoFichaje tipoEventoFichaje;
    private MetodoFichaje metodoFichaje;
    private LocalDateTime timeStampDispositivo;
    private Double latitud;
    private Double longitud;
    private String dispositivoId;
    private String comentario;
}