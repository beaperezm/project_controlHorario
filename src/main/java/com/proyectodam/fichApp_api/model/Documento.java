package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.EstadoFirma;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un documento almacenado en el sistema (nóminas,
 * contratos, etc.).
 */
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
    private String nombreArchivo;

    @Column(nullable = false)
    private String rutaAcceso; // Local: C:/| Cloud: URL Supabase

    @Column(nullable = false)
    private String tipoMime; // Tipo de archivo: application/pdf, image/png, etc.

    private long tamanoBytes;

    @Column(unique = true)
    private String hashDocumento; // Hash SHA-256 para asegurar la integridad del documento

    @Column(nullable = false, length = 100)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoFirma estadoFirma = EstadoFirma.PENDIENTE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaSubida;

    private LocalDateTime fechaFirma;
    
    private Integer anio;
    private Integer mes;
    private String etiquetas;

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

}
