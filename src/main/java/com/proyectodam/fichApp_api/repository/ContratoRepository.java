package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.Contrato;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Contrato c WHERE c.empleado.idEmpleado = :idEmpleado")
    void borrarEmpleadoPorId(@Param("idEmpleado") int idEmpleado);

}
