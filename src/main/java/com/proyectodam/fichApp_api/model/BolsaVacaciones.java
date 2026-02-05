package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bolsa_vacaciones")
public class BolsaVacaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bolsa_vac")
    private Integer idBolsaVac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "dias_totales_asignados")
    private Integer diasTotalesAsignados = 22;

    @Column(name = "dias_pendientes_anio_anterior")
    private Integer diasPendientesAnioAnterior = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // ===== GETTERS/SETTERS =====

    public Integer getIdBolsaVac() { return idBolsaVac; }
    public void setIdBolsaVac(Integer idBolsaVac) { this.idBolsaVac = idBolsaVac; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getDiasTotalesAsignados() { return diasTotalesAsignados; }
    public void setDiasTotalesAsignados(Integer diasTotalesAsignados) { this.diasTotalesAsignados = diasTotalesAsignados; }

    public Integer getDiasPendientesAnioAnterior() { return diasPendientesAnioAnterior; }
    public void setDiasPendientesAnioAnterior(Integer diasPendientesAnioAnterior) { this.diasPendientesAnioAnterior = diasPendientesAnioAnterior; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
