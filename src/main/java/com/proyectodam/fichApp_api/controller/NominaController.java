package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.dto.SignNominaRequestDTO;
import com.proyectodam.fichApp_api.service.IDocumentoService;
import com.proyectodam.fichApp_api.service.NominaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nominas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NominaController {

    private final NominaService nominaService;
    private final IDocumentoService documentoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> uploadNomina(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("mesAnio") String mesAnio,
            @RequestParam("idEmpleado") Integer idEmpleado) {

        return ResponseEntity.ok(nominaService.uploadNomina(archivo, mesAnio, idEmpleado));
    }

    @GetMapping("/usuario/{idEmpleado}")
    public ResponseEntity<List<DocumentoDTO>> listarNominasUsuario(@PathVariable Integer idEmpleado) {
        // Obtenemos solo los documentos que son NÓMINAS
        List<DocumentoDTO> nominas = documentoService.listarPorEmpleado(idEmpleado).stream()
                .filter(doc -> doc.getCategoria().equals("NOMINA"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(nominas);
    }

    @GetMapping("/{nominaId}/download")
    public ResponseEntity<Resource> descargarNomina(@PathVariable Long nominaId) {
        // En una aplicación real, aquí también se registraría el log de auditoría
        DocumentoDTO docInfo = documentoService.obtenerDetalles(nominaId);
        byte[] data = documentoService.descargarContenido(nominaId);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(docInfo.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docInfo.getNombreArchivo() + "\"")
                .body(resource);
    }

    @PostMapping("/{nominaId}/sign")
    public ResponseEntity<Void> signNomina(
            @PathVariable Long nominaId,
            @RequestParam Integer idEmpleado,
            @RequestBody SignNominaRequestDTO signRequest) {

        nominaService.signNomina(nominaId, idEmpleado, signRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{nominaId}/send-email")
    public ResponseEntity<Void> sendEmail(@PathVariable Long nominaId) {
        nominaService.sendEmail(nominaId);
        return ResponseEntity.ok().build();
    }
}
