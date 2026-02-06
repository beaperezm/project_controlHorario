package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface IDocumentoService {
    DocumentoDTO subirDocumento(MultipartFile archivo, CategoriaDocumento categoria, UUID idEmpleado);

    DocumentoDTO obtenerDetalles(Long id);

    List<DocumentoDTO> listarPorEmpleado(UUID idEmpleado);

    byte[] descargarContenido(Long id);

    // método para firmar el documento
    void firmarDocumento(Long id, UUID idEmpleado);

    void eliminarDocumento(Long id);
}
