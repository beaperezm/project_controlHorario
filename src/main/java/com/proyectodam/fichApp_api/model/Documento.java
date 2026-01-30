package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.enums.EstadoFirma;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreArchivo; // Ej: NOM_123_202511.pdf

    @Column(nullable = false)
    private String rutaAcceso; // Local: C:/... | Cloud: URL Supabase

    @Column(nullable = false)
    private String tipoMime; // application/pdf, image/png

    private long tamanoBytes;

    @Column(unique = true)
    private String hashDocumento; // SHA-256 para verificar integridad (Crucial para PRL/Nóminas)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaDocumento categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFirma estadoFirma;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaSubida;

    private LocalDateTime fechaFirma;

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

}
