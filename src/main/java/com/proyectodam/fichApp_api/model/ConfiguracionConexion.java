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

    @Column(name = "supa_url")
    private String supaUrl;

    @Column(name = "supa_key", length = 500)
    private String supaKey;

    @Column(name = "supa_db_pass")
    private String supaDbPass;

    @Column(name = "supa_db_user")
    private String supaDbUser = "postgres";

    @Column(name = "supa_db_name")
    private String supaDbName = "postgres";

    @Column(name = "supa_db_host")
    private String supaDbHost = "aws-0-eu-west-1.pooler.supabase.com";

    @Column(name = "requiere_reinicio")
    private boolean requiereReinicio = false;
}
