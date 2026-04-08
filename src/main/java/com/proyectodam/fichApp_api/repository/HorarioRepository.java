package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

}


