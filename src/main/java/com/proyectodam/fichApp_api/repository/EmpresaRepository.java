package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

}
