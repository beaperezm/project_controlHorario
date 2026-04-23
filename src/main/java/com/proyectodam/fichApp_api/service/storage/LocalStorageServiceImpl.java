package com.proyectodam.fichApp_api.service.storage;

import com.proyectodam.fichApp_api.exception.StorageException;
import com.proyectodam.fichApp_api.exception.StorageFileNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Implementación de almacenamiento LOCAL en el sistema de archivos.
 * Se activa cuando el perfil es "local", "server" o "remoteseed".
 */
@Service
@Profile({"default", "local", "server", "remoteseed"})
public class LocalStorageServiceImpl implements StorageService {

    private final Path rootLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("No se pudo crear el directorio de uploads", e);
        }
    }

    @Override
    public String store(MultipartFile file, String subDirectorio) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("No se puede guardar un archivo vacío");
            }

            Path targetDir = rootLocation;
            if (subDirectorio != null && !subDirectorio.isEmpty()) {
                targetDir = rootLocation.resolve(subDirectorio);
                Files.createDirectories(targetDir);
            }

            Path destinationFile = targetDir.resolve(file.getOriginalFilename()).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return destinationFile.toString();
        } catch (IOException e) {
            throw new StorageException("Error al guardar el archivo", e);
        }
    }

    @Override
    public byte[] loadBytes(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath);
            if (!Files.exists(filePath)) {
                throw new StorageFileNotFoundException("Archivo no encontrado: " + storagePath);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new StorageException("Error al leer el archivo: " + storagePath, e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo borrar " + storagePath + ": " + e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation));
        } catch (IOException e) {
            throw new StorageException("Error al listar archivos", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("No se pudo leer el archivo: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
