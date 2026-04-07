package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Empleado;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Contrato c WHERE c.empleado.idEmpleado = :idEmpleado")
    void borrarEmpleadoPorId(@Param("idEmpleado") int idEmpleado);

    @Query("SELECT c FROM Contrato c " + "JOIN FETCH c.empleado e " + "JOIN FETCH c.departamento d " + "JOIN FETCH c.rol r " +  "WHERE e.estado <> com.proyectodam.fichApp_api.enums.EstadoEmpleado.INACTIVO")
    List<Contrato> findContratosConEmpleadoActivo();

    Contrato findTopByEmpleadoOrderByFechaInicioDesc(Empleado empleado);

    @Query("SELECT c FROM Contrato c WHERE c.empleado.idEmpleado = :idEmpleado AND c.fechaInicio <= :hoy AND (c.fechaFin IS NULL OR c.fechaFin >= :hoy)")
    Optional<Contrato> buscarContratoActivo(@Param("idEmpleado") int idEmpleado, @Param("hoy") LocalDate hoy);


}
