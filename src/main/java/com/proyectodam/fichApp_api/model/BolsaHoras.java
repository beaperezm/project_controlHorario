package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bolsa_horas")
public class BolsaHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bolsa")
    private int idBolsa;

    @Column(name = "saldo_acumulado")
    private Double saldoHoras;
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    private int anio;

    @OneToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;




}
