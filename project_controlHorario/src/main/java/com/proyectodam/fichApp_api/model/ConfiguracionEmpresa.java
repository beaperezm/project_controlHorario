package com.proyectodam.fichApp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "configuraciones_empresa")
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private int idConfiguracion;

    @Column(name = "tolerancia_fichaje_min")
    private int toleranciaFichajeMin;

    @Column(name = "cierre_automatico_horas")
    private int cierreAutomaticoHoras;
    @Column(name = "requiere_geolocalizacion")
    private boolean requiereGeolocalizacion;
    @Column(name = "modo_quiosco_activo")
    private boolean modoQuioscoActivo;

    @Column(name = "tiempo_saludo_seg")
    private int tiempoSaludoSeg;

    @Column(name = "dias_aviso_cumpleanos")
    private int diasAvisoCumpleanos;

    @Column(name = "dias_aviso_contrato")
    private int diasAvisoContrato;

    @OneToOne
    @JoinColumn(name = "id_empresa", unique = true)
    private Empresa empresa;
}
