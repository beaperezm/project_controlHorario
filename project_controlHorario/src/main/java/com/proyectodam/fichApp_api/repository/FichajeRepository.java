package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {

}
