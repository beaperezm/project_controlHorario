package com.proyectodam.fichApp_api.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.proyectodam.fichApp_api.exception.StorageException;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    /**
     * Inicializa el servicio (crea carpetas locales o conecta con API Cloud).
     */
    void init();

    /**
     * Guarda un archivo en el sistema.
     * @param file El archivo subido por el usuario.
     * @param subDirectorio (Opcional) ID del empleado o categoría para organizar carpetas.
     * @return El path relativo o URL donde se guardó.
     */
    String store(MultipartFile file, String subDirectorio);

    /**
     * Carga todos los archivos (útil para administración).
     */
    Stream<Path> loadAll();

    /**
     * Obtiene la ruta de un archivo específico.
     */
    Path load(String filename);

    /**
     * Carga el archivo como recurso descargable (para el navegador).
     */
    Resource loadAsResource(String filename);

    /**
     * Elimina todos los archivos (útil para tests o limpieza).
     */
    void deleteAll();

    /**
     * Elimina un archivo concreto.
     */
    void delete(String filename);
}