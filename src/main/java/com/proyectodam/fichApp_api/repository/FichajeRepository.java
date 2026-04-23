package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {

    Optional<Fichaje> findTopByEmpleadoIdEmpleadoAndTimestampServidorBetweenOrderByTimestampServidorDesc(
            int idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    Optional<Fichaje> findTopByEmpleadoIdEmpleadoOrderByTimestampServidorDesc(int idEmpleado);

    List<Fichaje> findByEmpleadoIdEmpleadoAndTimestampServidorBetween(
            int idEmpleado, LocalDateTime inicio, LocalDateTime fin);

    List<Fichaje> findByEmpleadoIdEmpleado(int idEmpleado);
}
