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
public class FichajeDTO {

    private int idFichaje;
    private TipoEventoFichaje tipoEventoFichaje;
    private MetodoFichaje metodoFichaje;
    private LocalDateTime timestampServidor;
}
