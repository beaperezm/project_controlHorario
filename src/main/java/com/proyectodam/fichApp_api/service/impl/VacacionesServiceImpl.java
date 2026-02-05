package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.enums.EstadoSolicitud;
import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import com.proyectodam.fichApp_api.repository.SolicitudAusenciaRepository;
import com.proyectodam.fichApp_api.service.IVacacionesService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VacacionesServiceImpl implements IVacacionesService {

    private final SolicitudAusenciaRepository solicitudAusenciaRepository;

    public VacacionesServiceImpl(SolicitudAusenciaRepository solicitudAusenciaRepository) {
        this.solicitudAusenciaRepository = solicitudAusenciaRepository;
    }

    @Override
    public List<SolicitudAusencia> obtenerSolicitudesPorEmpleado(Integer idEmpleado) {
        return solicitudAusenciaRepository.findByEmpleado_IdEmpleado(idEmpleado);
    }

    @Override
    public SolicitudAusencia aprobarSolicitud(Integer idSolicitud) {
        SolicitudAusencia solicitud = solicitudAusenciaRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaResolucion(LocalDateTime.now());

        return solicitudAusenciaRepository.save(solicitud);
    }

    @Override
    public SolicitudAusencia rechazarSolicitud(Integer idSolicitud, String comentario) {
        SolicitudAusencia solicitud = solicitudAusenciaRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.DENEGADA); // 👈 según tu enum
        solicitud.setComentarioGestor(comentario);
        solicitud.setFechaResolucion(LocalDateTime.now());

        return solicitudAusenciaRepository.save(solicitud);
    }
}


