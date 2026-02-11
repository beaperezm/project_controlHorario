package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.service.IDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final IDocumentoService documentoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> uploadDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("categoria") CategoriaDocumento categoria,
            @RequestParam("idEmpleado") Integer idEmpleado) {
        DocumentoDTO documento = documentoService.subirDocumento(archivo, categoria, idEmpleado);
        return ResponseEntity.ok(documento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> getDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerDetalles(id));
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<DocumentoDTO>> listarPorEmpleado(@PathVariable Integer idEmpleado) {
        return ResponseEntity.ok(documentoService.listarPorEmpleado(idEmpleado));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id) {
        DocumentoDTO meta = documentoService.obtenerDetalles(id);
        byte[] data = documentoService.descargarContenido(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getNombreArchivo() + "\"")
                .contentType(MediaType.parseMediaType(meta.getTipoMime()))
                .body(data);
    }

    @PutMapping("/{id}/firmar")
    public ResponseEntity<Void> firmarDocumento(@PathVariable Long id, @RequestParam Integer idEmpleado) {
        documentoService.firmarDocumento(id, idEmpleado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoService.eliminarDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
