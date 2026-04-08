package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.MetodoFichaje;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "fichajes")
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fichaje")
    private int idFichaje;

    @Column(name = "timestamp_dispositivo")
    private LocalDateTime timestampDispositivo;

    @Column(name = "timestamp_servidor")
    private LocalDateTime timestampServidor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento")
    private TipoEventoFichaje tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_registro")
    private MetodoFichaje metodoRegistro;

    @Column(name = "dispositivo_id")
    private String dispositivoId;

    @Column(name = "geolocalizacion_lat")
    private Float geolocalizacionLat;

    @Column(name = "geolocalizacion_long")
    private Float geolocalizacionLong;

    @Column(name = "es_valido")
    private Boolean esValido;

    @Column(name = "es_modificado")
    private Boolean esModificado;

    private String comentario;
    private Boolean sincronizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

}
