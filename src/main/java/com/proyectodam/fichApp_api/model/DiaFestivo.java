package com.proyectodam.fichApp_api.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "dias_festivos")
public class DiaFestivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_festivo")
    private Integer idFestivo;

    // Si ya tienes CentroTrabajo Entity, cámbialo a ManyToOne
    @Column(name = "id_centro")
    private Integer idCentro;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "anio")
    private Integer anio;

    public Integer getIdFestivo() { return idFestivo; }
    public void setIdFestivo(Integer idFestivo) { this.idFestivo = idFestivo; }

    public Integer getIdCentro() { return idCentro; }
    public void setIdCentro(Integer idCentro) { this.idCentro = idCentro; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

}
