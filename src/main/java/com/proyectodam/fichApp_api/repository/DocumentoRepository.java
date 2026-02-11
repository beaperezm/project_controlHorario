package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
  List<Documento> findByEmpleadoIdEmpleado(Integer idEmpleado);

  List<Documento> findByCategoria(CategoriaDocumento categoria);
}
