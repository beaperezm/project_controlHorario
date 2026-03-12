package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.SolicitudAusenciaCreateDTO;
import com.proyectodam.fichApp_api.enums.EstadoSolicitud;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import com.proyectodam.fichApp_api.repository.SolicitudAusenciaRepository;
import com.proyectodam.fichApp_api.service.IVacacionesService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VacacionesServiceImpl implements IVacacionesService {

    private final SolicitudAusenciaRepository repo;

    public VacacionesServiceImpl(SolicitudAusenciaRepository repo) {
        this.repo = repo;
    }

    @Override
    public SolicitudAusencia crearSolicitud(SolicitudAusenciaCreateDTO dto) {
        if (dto.getIdEmpleado() == null) throw new IllegalArgumentException("idEmpleado obligatorio");
        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) throw new IllegalArgumentException("fechas obligatorias");
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) throw new IllegalArgumentException("fechaFin no puede ser < fechaInicio");
        if (dto.getTipo() == null || dto.getTipo().isBlank()) throw new IllegalArgumentException("tipo obligatorio");

        SolicitudAusencia s = new SolicitudAusencia();

        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(dto.getIdEmpleado()); // ✅ ahora coincide (UUID)
        s.setEmpleado(empleado);

        s.setFechaInicio(dto.getFechaInicio());
        s.setFechaFin(dto.getFechaFin());
        s.setTipo(dto.getTipo());

        s.setEstado(EstadoSolicitud.PENDIENTE);
        s.setFechaSolicitud(LocalDateTime.now());

        if (dto.getComentario() != null && !dto.getComentario().isBlank()) {
            s.setComentarioGestor(dto.getComentario());
        }

        return repo.save(s);
    }

    @Override
    public List<SolicitudAusencia> obtenerSolicitudesPorEmpleado(UUID idEmpleado) {
        return repo.findByEmpleado_IdEmpleado(idEmpleado);
    }

    @Override
    public SolicitudAusencia aprobarSolicitud(Integer idSolicitud) {
        SolicitudAusencia s = repo.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no existe: " + idSolicitud));

        s.setEstado(EstadoSolicitud.APROBADA);
        s.setFechaResolucion(LocalDateTime.now());
        return repo.save(s);
    }

    @Override
    public SolicitudAusencia rechazarSolicitud(Integer idSolicitud, String comentario) {
        SolicitudAusencia s = repo.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no existe: " + idSolicitud));

        s.setEstado(EstadoSolicitud.DENEGADA);
        s.setFechaResolucion(LocalDateTime.now());
        s.setComentarioGestor(comentario);

        return repo.save(s);
    }
}