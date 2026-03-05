package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private int idContrato;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "tipo_contrato", length = 50)
    private String tipoContrato;

    @Column(name = "horas_semanales")
    private LocalTime horasSemanales;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "id_empleado") // un empleado puede tener muchos contratos (renovaciones, etc) - un contrato
                                      // pertenece a un solo empleado
    private Empleado empleado;

    @ManyToOne // Un rol puede estar en muchos contratos - cada contrato tiene un rol
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_departamento") // Un departamento tiene muchos contratos - el empleado puede cambiar de
                                          // departamento con otro contrato
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "id_horario") // un horario puede asignarse a muchos contratos - un contrato define qué
                                     // horario se aplica
    private Horario horario;

}
