package com.proyectodam.fichApp_api.dto;

import java.time.LocalDate;
import java.util.UUID;

public class SolicitudAusenciaCreateDTO {

    private UUID idEmpleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipo;
    private String comentario; // si lo usas en el service

    public UUID getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(UUID idEmpleado) { this.idEmpleado = idEmpleado; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}