package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.enums.EstadoSolicitud;
import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.repository.SolicitudAusenciaRepository;
import com.proyectodam.fichApp_api.service.IVacacionesService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VacacionesServiceImpl implements IVacacionesService {

    private final SolicitudAusenciaRepository solicitudAusenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final com.proyectodam.fichApp_api.service.EmailService emailService;

    @Value("${fichapp.vacaciones.contar-fines-semana:true}")
    private boolean contarFinesDeSemana;

    public VacacionesServiceImpl(SolicitudAusenciaRepository solicitudAusenciaRepository, EmpleadoRepository empleadoRepository, com.proyectodam.fichApp_api.service.EmailService emailService) {
        this.solicitudAusenciaRepository = solicitudAusenciaRepository;
        this.empleadoRepository = empleadoRepository;
        this.emailService = emailService;
    }

    @Override
    public List<SolicitudAusencia> obtenerTodasLasSolicitudes() {
        return solicitudAusenciaRepository.findAll();
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

        SolicitudAusencia guardada = solicitudAusenciaRepository.save(solicitud);
        
        if (guardada.getEmpleado() != null && guardada.getEmpleado().getEmail() != null) {
            String periodo = guardada.getFechaInicio() + " a " + guardada.getFechaFin();
            emailService.sendEstadoVacacionesEmail(guardada.getEmpleado().getEmail(), periodo, "Aprobada", null);
        }

        return guardada;
    }

    @Override
    public SolicitudAusencia rechazarSolicitud(Integer idSolicitud, String comentario) {
        SolicitudAusencia solicitud = solicitudAusenciaRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.DENEGADA); // 👈 según tu enum
        solicitud.setComentarioGestor(comentario);
        solicitud.setFechaResolucion(LocalDateTime.now());

        SolicitudAusencia guardada = solicitudAusenciaRepository.save(solicitud);

        if (guardada.getEmpleado() != null && guardada.getEmpleado().getEmail() != null) {
            String periodo = guardada.getFechaInicio() + " a " + guardada.getFechaFin();
            emailService.sendEstadoVacacionesEmail(guardada.getEmpleado().getEmail(), periodo, "Denegada", comentario);
        }

        return guardada;
    }

    @Override
    public SolicitudAusencia crearSolicitud(Integer idEmpleado, SolicitudAusencia peticion) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id " + idEmpleado));

        peticion.setEmpleado(empleado);
        peticion.setEstado(EstadoSolicitud.PENDIENTE);
        peticion.setFechaSolicitud(LocalDateTime.now());
        
        // Calcular días hábiles basado en la configuración
        
        if (peticion.getDiasHabiles() == null || peticion.getDiasHabiles() == 0) {
            long dias = 0;
            LocalDate current = peticion.getFechaInicio();
            LocalDate end = peticion.getFechaFin();
            
            while (!current.isAfter(end)) {
                if (contarFinesDeSemana || 
                   (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY)) {
                    dias++;
                }
                current = current.plusDays(1);
            }
            peticion.setDiasHabiles((int) dias);
        }

        return solicitudAusenciaRepository.save(peticion);
    }
}
