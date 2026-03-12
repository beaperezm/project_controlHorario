package com.proyectodam.fichApp_api.dto;

import com.proyectodam.fichApp_api.enums.EstadoSolicitud;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VacacionesDTO {
    private Integer idSolicitud;
    private Integer idEmpleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasHabiles;
    private String tipo;
    private EstadoSolicitud estado;
    private String comentarioGestor;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;

    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getDiasHabiles() { return diasHabiles; }
    public void setDiasHabiles(Integer diasHabiles) { this.diasHabiles = diasHabiles; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public String getComentarioGestor() { return comentarioGestor; }
    public void setComentarioGestor(String comentarioGestor) { this.comentarioGestor = comentarioGestor; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}