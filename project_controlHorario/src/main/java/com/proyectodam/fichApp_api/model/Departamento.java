package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private int idDepartamento;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne //una empresa tiene muchos departamentos, el nombre de un departamento pertenece a una sola empresa
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
}
