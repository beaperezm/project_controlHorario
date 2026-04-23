package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidad que representa la empresa cliente que utiliza el sistema.
 */
@Entity
@Table(name = "empresas")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 20)
    private String cif;

    private String direccion;
    private String telefono;
    private String emailContacto;
    private String logoUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
}
