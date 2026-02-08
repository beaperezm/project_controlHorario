package com.proyectodam.fichApp_api.model;


import com.proyectodam.fichApp_api.enums.EstadoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_ausencia")
public class SolicitudAusencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    // FK -> empleados(id_empleado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    // FK -> empleados(id_empleado) (aprobador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aprobador")
    private Empleado aprobador;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "dias_habiles")
    private Integer diasHabiles;

    // En BD es varchar(50). Para vacaciones usa "VACACIONES"
    @Column(name = "tipo", length = 50)
    private String tipo;

    // Postgres enum estado_solicitud
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "comentario_gestor", columnDefinition = "text")
    private String comentarioGestor;

    // ===== GETTERS/SETTERS =====

    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Empleado getAprobador() { return aprobador; }
    public void setAprobador(Empleado aprobador) { this.aprobador = aprobador; }

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

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getComentarioGestor() { return comentarioGestor; }
    public void setComentarioGestor(String comentarioGestor) { this.comentarioGestor = comentarioGestor; }

}
