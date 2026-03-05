package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private int idHorario;

    private String nombre;

    @Column(name = "configuracion_semanal", columnDefinition = "TEXT")
    private String configuracionSemanal;

    @Column(name = "margen_flexible_min")
    private int margenFlexibleMin;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
}
