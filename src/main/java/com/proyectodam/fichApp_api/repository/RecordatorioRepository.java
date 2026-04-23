package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Recordatorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    List<Recordatorio> findByFechaBetweenOrderByFechaAsc(LocalDate start, LocalDate end);

    List<Recordatorio> findByIdEmpleadoAndFechaBetweenOrderByFechaAsc(Integer idEmpleado, LocalDate start,
            LocalDate end);
}
