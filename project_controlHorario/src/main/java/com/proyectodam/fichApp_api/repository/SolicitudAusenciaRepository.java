package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolicitudAusenciaRepository
        extends JpaRepository<SolicitudAusencia, Integer> {

    List<SolicitudAusencia> findByEmpleado_IdEmpleado(Integer idEmpleado);
}
