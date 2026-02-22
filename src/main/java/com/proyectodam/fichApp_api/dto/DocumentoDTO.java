package com.proyectodam.fichApp_api.dto;

import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO que representa la información de un documento para enviar al cliente.
 * Oculta detalles internos de almacenamiento y expone URLs de descarga.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDTO {
    private Long id;
    private String nombreArchivo;
    private String tipoMime;
    private long tamanoBytes;
    private CategoriaDocumento categoria;
    private LocalDateTime fechaSubida;
    private String urlDescarga;
    private Integer idEmpleado;
}
