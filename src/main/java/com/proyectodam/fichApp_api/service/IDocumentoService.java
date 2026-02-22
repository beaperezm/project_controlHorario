package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.List;

/**
 * Servicio para la gestión de documentos, incluyendo subida, descarga y firma.
 */
public interface IDocumentoService {
    /**
     * Sube un archivo al servidor y lo asocia a un empleado.
     * 
     * @param archivo    El archivo físico a subir.
     * @param categoria  Categoría del documento (Nómina, Contrato, etc.).
     * @param idEmpleado ID del empleado propietario del documento.
     * @return DTO con la información del documento creado.
     */
    DocumentoDTO subirDocumento(MultipartFile archivo, CategoriaDocumento categoria, Integer idEmpleado);

    /**
     * Obtiene los detalles de un documento.
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
