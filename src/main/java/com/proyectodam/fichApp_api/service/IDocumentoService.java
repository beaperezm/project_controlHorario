package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.List;

public interface IDocumentoService {
    DocumentoDTO subirDocumento(MultipartFile archivo, CategoriaDocumento categoria, Integer idEmpleado);

    DocumentoDTO obtenerDetalles(Long id);

    List<DocumentoDTO> listarPorEmpleado(Integer idEmpleado);

    byte[] descargarContenido(Long id);

    // método para firmar el documento
    void firmarDocumento(Long id, Integer idEmpleado);

    void eliminarDocumento(Long id);
}
