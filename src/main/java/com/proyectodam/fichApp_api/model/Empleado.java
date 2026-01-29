package com.proyectodam.pickApp_api.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @Column(name = "id_empleado")
    private UUID idEmpleado;

    // GETTER / SETTER
    public UUID getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(UUID idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
