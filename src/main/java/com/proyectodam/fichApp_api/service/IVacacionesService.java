package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.model.SolicitudAusencia;

import java.util.List;

public interface IVacacionesService {

    List<SolicitudAusencia> obtenerSolicitudesPorEmpleado(Integer idEmpleado);

    SolicitudAusencia aprobarSolicitud(Integer idSolicitud);

    SolicitudAusencia rechazarSolicitud(Integer idSolicitud, String comentario);

}
