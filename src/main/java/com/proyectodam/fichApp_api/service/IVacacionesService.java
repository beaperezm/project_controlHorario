package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.SolicitudAusenciaCreateDTO;
import com.proyectodam.fichApp_api.model.SolicitudAusencia;

import java.util.List;
import java.util.UUID;

public interface IVacacionesService {

    SolicitudAusencia crearSolicitud(SolicitudAusenciaCreateDTO dto);

    List<SolicitudAusencia> obtenerSolicitudesPorEmpleado(UUID idEmpleado);

    SolicitudAusencia aprobarSolicitud(Integer idSolicitud);

    SolicitudAusencia rechazarSolicitud(Integer idSolicitud, String comentario);
}