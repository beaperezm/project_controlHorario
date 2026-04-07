package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByEmail(String email);
    Optional<Empleado> findByDniNie(String dniNie);

    List<Empleado> findByEstadoNot(EstadoEmpleado estadoEmpleados);

    long countByEstado(EstadoEmpleado estadoEmpleado);

    Optional<Empleado> findByAuthUserId(String userId);
}
