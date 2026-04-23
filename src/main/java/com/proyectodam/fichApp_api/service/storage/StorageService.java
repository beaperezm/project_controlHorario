package com.proyectodam.fichApp_api.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    /**
     * Inicializa el servicio (crea carpetas locales o verifica bucket Cloud).
     */
    void init();

    /**
     * Guarda un archivo en el sistema.
     * @param file El archivo subido por el usuario.
     * @param subDirectorio (Opcional) ID del empleado o categoría para organizar.
     * @return El path relativo o URL donde se guardó.
     */
    String store(MultipartFile file, String subDirectorio);

    /**
     * Descarga el contenido de un archivo como array de bytes.
     * @param storagePath El path devuelto por store().
     * @return Los bytes del archivo.
     */
    byte[] loadBytes(String storagePath);

    /**
     * Elimina un archivo concreto.
     * @param storagePath El path devuelto por store().
     */
    void delete(String storagePath);

    /**
     * Carga todos los archivos (útil para administración). Solo local.
     */
    Stream<Path> loadAll();

    /**
     * Obtiene la ruta de un archivo específico. Solo local.
     */
    Path load(String filename);

    /**
     * Carga el archivo como recurso descargable (para el navegador). Solo local.
     */
    Resource loadAsResource(String filename);

    /**
     * Elimina todos los archivos (útil para tests o limpieza).
     */
    void deleteAll();
}