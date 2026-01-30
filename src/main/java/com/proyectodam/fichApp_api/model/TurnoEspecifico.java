package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "turnos_especificos")
public class TurnoEspecifico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private int idTurno;

    private LocalDate fecha;
    private Boolean esFestivoPropio;
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;


    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horario horario;
}
