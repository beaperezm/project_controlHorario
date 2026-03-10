package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    // Extraer recordatorios dentro de un mes (buscando entre el dia 1 y el fin de
    // mes)
    List<Recordatorio> findByFechaBetweenOrderByFechaAsc(LocalDate start, LocalDate end);

    List<Recordatorio> findByIdEmpleadoAndFechaBetweenOrderByFechaAsc(Integer idEmpleado, LocalDate start,
            LocalDate end);

}
