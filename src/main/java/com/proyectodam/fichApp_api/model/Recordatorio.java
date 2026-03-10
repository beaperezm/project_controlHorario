package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "recordatorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha; // Solo fecha para anclarlo a un día del calendario

    @Column(name = "id_empleado")
    private Integer idEmpleado; // Opcional, para saber quién lo creó o asignárselo a alguien

}
