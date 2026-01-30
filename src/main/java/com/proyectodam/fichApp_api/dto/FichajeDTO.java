package com.proyectodam.fichApp_api.dto;

import com.proyectodam.fichApp_api.enums.MetodoFichaje;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


//El DTO en este proyecto sirve para enviar y recibir datos entre: el backend -- Frontend
//                                                                 el backend -- API REST

//Se necesita esta clase DTO porque con el model estaría: exponiendo toda la entidad, exponiendo relaciones, podría tener bucles infinitos en JSON, y mostraría datos que no quiero (pass, PIN..)
@AllArgsConstructor
@NoArgsConstructor
@Getter //sólo transfiere información necesaria al frontend
@Setter //sólo transfiere información necesaria al frontend
public class FichajeDTO {

    private int idFichaje;
    private TipoEventoFichaje tipoEventoFichaje;
    private MetodoFichaje metodoFichaje;
    private LocalDateTime timestampServidor;
}
