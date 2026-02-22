package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion_conexion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionConexion {

    @Id
    private Long id = 1L; // Solo una configuración global

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModoConexion modo = ModoConexion.LOCAL;

    @Column(name = "url_personalizada")
    private String urlPersonalizada;

    @Column(name = "requiere_reinicio")
    private boolean requiereReinicio = false;
}
