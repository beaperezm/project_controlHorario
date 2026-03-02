package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.BolsaHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BolsaHorasRepository extends JpaRepository<BolsaHoras, Integer> {

    BolsaHoras findByEmpleado_IdEmpleado(int id);

}