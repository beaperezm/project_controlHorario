package com.proyectodam.fichApp_api.exception;

/**
 * Excepción específica lanzada cuando no se encuentra un archivo solicitado.
 */
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}