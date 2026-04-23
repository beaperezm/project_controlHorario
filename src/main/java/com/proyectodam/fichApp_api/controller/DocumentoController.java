package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.service.IDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Revisar
public class DocumentoController {

    private final IDocumentoService documentoService;

    /**
     * Sube un nuevo documento al sistema.
     * Recibe el archivo, su categoría y el ID del empleado al que pertenece.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> subirDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "nombreCustom", required = false) String nombreCustom,
            @RequestParam("categoria") String categoria,
            @RequestParam("idEmpleado") Integer idEmpleado,
            @RequestParam(value = "anio", required = false) Integer anio,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "etiquetas", required = false) String etiquetas) {

        DocumentoDTO nuevoDocumento = documentoService.subirDocumento(archivo, nombreCustom, categoria, idEmpleado, anio, mes, etiquetas);
        return ResponseEntity.ok(nuevoDocumento);
    }

    /**
     * Obtiene los detalles de un documento específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> obtenerDetalles(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerDetalles(id));
    }

    /**
     * Lista todos los documentos de la plataforma (acceso sugerido a
     * administrador).
     */
    @GetMapping("/all")
    public ResponseEntity<List<DocumentoDTO>> listarTodos() {
        return ResponseEntity.ok(documentoService.listarTodos());
    }

    /**
     * Lista todos los documentos asociados a un empleado.
     */
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<DocumentoDTO>> listarPorEmpleado(@PathVariable Integer idEmpleado) {
        return ResponseEntity.ok(documentoService.listarPorEmpleado(idEmpleado));
    }

    /**
     * Obtiene los años ("tiempos") únicos disponibles.
     */
    @GetMapping("/tiempo")
    public ResponseEntity<List<String>> obtenerTiemposDisponibles(@RequestParam(defaultValue = "NOMINA") String categoria) {
        return ResponseEntity.ok(documentoService.getTiemposDisponibles(categoria));
    }

    /**
     * Obtiene los documentos de forma paginada para ser consumidos desde el cliente.
     */
    @GetMapping("/paginas")
    public ResponseEntity<org.springframework.data.domain.Page<DocumentoDTO>> obtenerPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "NOMINA") String categoria,
            @RequestParam(required = false) Integer idEmpleado,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(documentoService.obtenerPaginados(page, size, categoria, idEmpleado, searchQuery, year));
    }

    /**
     * Permite descargar el contenido de un documento.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        DocumentoDTO docInfo = documentoService.obtenerDetalles(id);
        byte[] data = documentoService.descargarContenido(id);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(docInfo.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docInfo.getNombreArchivo() + "\"")
                .body(resource);
    }

    /**
     * Marca un documento como firmado por el empleado.
     */
    @PutMapping("/{id}/firmar")
    public ResponseEntity<Void> firmarDocumento(@PathVariable Long id, @RequestParam Integer idEmpleado) {
        documentoService.firmarDocumento(id, idEmpleado);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un documento del sistema.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoService.eliminarDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
