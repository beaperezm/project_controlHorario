package com.proyectodam.fichApp_api.dto;

import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
