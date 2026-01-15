package com.fichapp.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "centros_trabajo")
@Data
public class CentroTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCentro;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    private String nombre;
    private String direccion;
    private String codigoPostal;
}