package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.enums.EstadoSolicitud;
import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudAusenciaRepository extends JpaRepository<SolicitudAusencia, Integer> {

    List<SolicitudAusencia> findByEmpleado_IdEmpleado(UUID idEmpleado);

    List<SolicitudAusencia> findByEstado(EstadoSolicitud estado);
}