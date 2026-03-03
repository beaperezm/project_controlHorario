package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Servicio para la gestión de documentos, incluyendo subida, descarga y firma.
 */
public interface IDocumentoService {
    /**
     * Sube un nuevo documento y genera su Hash SHA-256.
     */
    DocumentoDTO subirDocumento(MultipartFile archivo, String nombreCustom, String categoria, Integer idEmpleado);

    /**
     * Devuelve la lista de todos los documentos en el sistema.
     */
    List<DocumentoDTO> listarTodos();

    /**
     * Devuelve los detalles de un documento específico.
     */
    DocumentoDTO obtenerDetalles(Long id);

    /**
     * Lista todos los documentos pertenecientes a un empleado.
     */
    List<DocumentoDTO> listarPorEmpleado(Integer idEmpleado);

    /**
     * Descarga el contenido binario de un documento.
     */
    byte[] descargarContenido(Long id);

    /**
     * Realiza el proceso de firma digital (simulada) de un documento por parte del
     * empleado.
     */
    void firmarDocumento(Long id, Integer idEmpleado);

    /**
     * Elimina un documento del sistema.
     */
    void eliminarDocumento(Long id);
}
