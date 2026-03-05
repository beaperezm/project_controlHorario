package com.proyectodam.fichApp_api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auditoria_fichajes")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private int idAudit;

    @ManyToOne
    @JoinColumn(name = "id_fichaje")
    private Fichaje fichaje;

    @ManyToOne
    @JoinColumn(name = "id_operador")
    private Empleado operador;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(name = "valor_anterior")
    private LocalDateTime valorAnterior;

    @Column(name = "valor_nuevo")
    private LocalDateTime valorNuevo;

    private String motivoCambio;

}
