package com.proyectodam.fichApp_api.dto;

import java.time.LocalDate;

public class SolicitudAusenciaCreateDTO {

    private Integer idEmpleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipo; // VACACIONES, ASUNTOS_PROPIOS, etc.

    // getters y setters
    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
