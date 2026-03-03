package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

  /**
   * Busca todos los documentos asociados a un empleado específico.
   */
  List<Documento> findByEmpleadoIdEmpleado(Integer idEmpleado);

  /**
   * Busca documentos por categoría.
   */
  List<Documento> findByCategoria(String categoria);

  /**
   * Verifica si un documento ya existe dado su Hash SHA-256.
   */
  boolean existsByHashDocumento(String hashDocumento);
}
