package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  /**
   * Obtiene los años únicos en los que se han creado/subido documentos de cierta categoría.
   */
  @Query("SELECT DISTINCT d.anio FROM Documento d WHERE d.categoria = :categoria AND d.anio IS NOT NULL ORDER BY d.anio DESC")
  List<Integer> findDistinctUploadYears(@Param("categoria") String categoria);

  /**
   * Paginación dinámica: Filtra por categoría, ID Empleado (opcional), término de búsqueda y año.
   */
  @Query("SELECT d FROM Documento d WHERE d.categoria = :categoria " +
         "AND (:idEmpleado IS NULL OR d.empleado.idEmpleado = :idEmpleado) " +
         "AND (:searchQuery IS NULL OR LOWER(CAST(d.nombreArchivo AS string)) LIKE LOWER(CAST(CONCAT('%', :searchQuery, '%') AS string)) " +
         "    OR (d.empleado IS NOT NULL AND (LOWER(CAST(d.empleado.nombre AS string)) LIKE LOWER(CAST(CONCAT('%', :searchQuery, '%') AS string)) " +
         "    OR LOWER(CAST(d.empleado.apellidos AS string)) LIKE LOWER(CAST(CONCAT('%', :searchQuery, '%') AS string))))) " +
         "AND (:year IS NULL OR d.anio = :year)")
  Page<Documento> findPaginatedWithFilters(
      @Param("categoria") String categoria,
      @Param("idEmpleado") Integer idEmpleado,
      @Param("searchQuery") String searchQuery,
      @Param("year") Integer year,
      Pageable pageable
  );
}
