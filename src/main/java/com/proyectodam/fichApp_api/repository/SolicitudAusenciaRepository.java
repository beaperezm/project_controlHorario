package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudAusenciaRepository
        extends JpaRepository<SolicitudAusencia, Integer> {

    List<SolicitudAusencia> findByEmpleado_IdEmpleado(Integer idEmpleado);
}
