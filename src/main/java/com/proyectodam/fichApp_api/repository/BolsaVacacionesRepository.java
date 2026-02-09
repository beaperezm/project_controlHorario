package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.BolsaVacaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BolsaVacacionesRepository extends JpaRepository<BolsaVacaciones, Integer> {

}
