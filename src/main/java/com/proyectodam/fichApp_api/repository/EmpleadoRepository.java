package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {


}
