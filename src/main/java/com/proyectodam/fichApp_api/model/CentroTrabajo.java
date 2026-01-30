package com.proyectodam.fichApp_api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "centros_trabajo")
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
