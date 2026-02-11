package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.enums.EstadoFirma;
import com.proyectodam.fichApp_api.model.Documento;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.DocumentoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.service.IDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements IDocumentoService {

    private final DocumentoRepository documentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public DocumentoDTO subirDocumento(MultipartFile archivo, CategoriaDocumento categoria, Integer idEmpleado) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        String fileName = StringUtils.cleanPath(archivo.getOriginalFilename());

        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(archivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String hash = calcularHash(archivo.getBytes());

            Documento documento = Documento.builder()
                    .nombreArchivo(fileName)
                    .rutaAcceso(targetLocation.toString())
                    .tipoMime(archivo.getContentType())
                    .tamanoBytes(archivo.getSize())
                    .categoria(categoria)
                    .estadoFirma(EstadoFirma.PENDIENTE)
                    .hashDocumento(hash)
                    .empleado(empleado)
                    .build();

            Documento guardado = documentoRepository.save(documento);
            return mapToDTO(guardado);
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error al almacenar el archivo " + fileName, ex);
        }
    }

    @Override
    public DocumentoDTO obtenerDetalles(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        return mapToDTO(documento);
    }

    @Override
    public List<DocumentoDTO> listarPorEmpleado(Integer idEmpleado) {
        return documentoRepository.findByEmpleadoIdEmpleado(idEmpleado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] descargarContenido(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        try {
            Path filePath = Paths.get(documento.getRutaAcceso());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
    }

    @Override
    public void firmarDocumento(Long id, Integer idEmpleado) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        if (!java.util.Objects.equals(documento.getEmpleado().getIdEmpleado(), idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para firmar este documento");
        }

        documento.setEstadoFirma(EstadoFirma.FIRMADO);
        documento.setFechaFirma(LocalDateTime.now());
        documentoRepository.save(documento);
    }

    @Override
    public void eliminarDocumento(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        try {
            Path filePath = Paths.get(documento.getRutaAcceso());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo borrar el archivo físico " + documento.getRutaAcceso());
        }

        documentoRepository.delete(documento);
    }

    private String calcularHash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(content);
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private DocumentoDTO mapToDTO(Documento documento) {
        return DocumentoDTO.builder()
                .id(documento.getId())
                .nombreArchivo(documento.getNombreArchivo())
                .tipoMime(documento.getTipoMime())
                .tamanoBytes(documento.getTamanoBytes())
                .categoria(documento.getCategoria())
                .fechaSubida(documento.getFechaSubida())
                .urlDescarga("/api/documentos/" + documento.getId() + "/download")
                .idEmpleado(documento.getEmpleado() != null ? documento.getEmpleado().getIdEmpleado() : null)
                .build();
    }
}
