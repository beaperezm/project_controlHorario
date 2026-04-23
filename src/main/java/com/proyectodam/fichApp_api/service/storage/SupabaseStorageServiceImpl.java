package com.proyectodam.fichApp_api.service.storage;

import com.proyectodam.fichApp_api.exception.StorageException;
import com.proyectodam.fichApp_api.exception.StorageFileNotFoundException;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import com.proyectodam.fichApp_api.repository.ConfiguracionConexionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

/**
 * Implementación de almacenamiento en SUPABASE STORAGE (Cloud).
 * Usa la API REST de Supabase Storage para subir/descargar/borrar archivos
 * en un bucket llamado "documentos".
 * <p>
 * Se activa únicamente cuando el perfil "supabase" está activo.
 */
@Service
@Profile("supabase")
public class SupabaseStorageServiceImpl implements StorageService {

    private static final String BUCKET_NAME = "documentos";

    @Autowired
    private ConfiguracionConexionRepository configRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Value("${supabase.rest-url:}")
    private String supabaseUrlProp;

    @Value("${supabase.rest-key:}")
    private String supabaseKeyProp;

    private String supabaseUrl;
    private String supabaseKey;

    @PostConstruct
    public void init() {
        try {
            // 1. Intentar cargar desde la Base de Datos
            ConfiguracionConexion config = configRepository.obtenerConfiguracion().orElse(null);

            if (config != null && config.getSupaUrl() != null && !config.getSupaUrl().isEmpty()) {
                this.supabaseUrl = config.getSupaUrl();
                this.supabaseKey = config.getSupaKey();
                System.out.println("Supabase Storage: Usando configuración de la Base de Datos.");
            } 
            // 2. Fallback a propiedades (application.properties / Variables de Entorno)
            else if (supabaseUrlProp != null && !supabaseUrlProp.isEmpty()) {
                this.supabaseUrl = supabaseUrlProp;
                this.supabaseKey = supabaseKeyProp;
                System.out.println("Supabase Storage: Usando configuración de propiedades/entorno.");
            }

            if (supabaseUrl != null && !supabaseUrl.isEmpty() && supabaseKey != null && !supabaseKey.isEmpty()) {
                // Limpiar URL
                if (supabaseUrl.endsWith("/")) {
                    supabaseUrl = supabaseUrl.substring(0, supabaseUrl.length() - 1);
                }

                // Verificar/crear bucket
                crearBucketSiNoExiste();
                System.out.println("Supabase Storage inicializado. Bucket: " + BUCKET_NAME);
            } else {
                System.out.println("Supabase Storage: No hay configuración válida (URL/Key vacíos). El almacenamiento estará deshabilitado.");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar Supabase Storage: " + e.getMessage());
        }
    }

    @Override
    public String store(MultipartFile file, String subDirectorio) {
        try {
            if (supabaseUrl == null || supabaseUrl.isEmpty()) {
                throw new StorageException("No se puede subir el archivo: La URL de Supabase no está configurada.");
            }

            String fileName = file.getOriginalFilename();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            
            String objectPath = subDirectorio != null && !subDirectorio.isEmpty() ? 
                                subDirectorio + "/" + fileName : fileName;
            
            String encodedObjectPath = subDirectorio != null && !subDirectorio.isEmpty() ? 
                                       subDirectorio + "/" + encodedFileName : encodedFileName;

            String url = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + encodedObjectPath;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("Content-Type", file.getContentType())
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Archivo subido a Supabase Storage: " + objectPath);
                return objectPath; // Guardamos el path relativo en la BD
            } else {
                throw new StorageException("Error al subir archivo a Supabase Storage. Código: "
                        + response.statusCode() + " - " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new StorageException("Error de comunicación con Supabase Storage", e);
        }
    }

    @Override
    public byte[] loadBytes(String storagePath) {
        try {
            if (supabaseUrl == null || supabaseUrl.isEmpty()) {
                throw new StorageException("No se puede descargar el archivo: La URL de Supabase no está configurada.");
            }

            // Codificar el path de almacenamiento para la URL
            String[] parts = storagePath.split("/");
            StringBuilder encodedPath = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                encodedPath.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8).replace("+", "%20"));
                if (i < parts.length - 1) encodedPath.append("/");
            }

            String url = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + encodedPath.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 404) {
                throw new StorageFileNotFoundException("Archivo no encontrado en Supabase Storage: " + storagePath);
            } else {
                throw new StorageException("Error al descargar de Supabase Storage. Código: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new StorageException("Error de comunicación con Supabase Storage", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            // La API de Supabase Storage para borrar usa DELETE con un body JSON
            String url = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME;
            String jsonBody = "{\"prefixes\":[\"" + storagePath + "\"]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("Content-Type", "application/json")
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Archivo eliminado de Supabase Storage: " + storagePath);
            } else {
                System.err.println("No se pudo borrar de Supabase Storage: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al borrar de Supabase Storage: " + e.getMessage());
        }
    }

    // --- Métodos no aplicables en Cloud (solo para compatibilidad de interfaz) ---

    @Override
    public Stream<Path> loadAll() {
        throw new UnsupportedOperationException("loadAll() no está soportado en Supabase Storage");
    }

    @Override
    public Path load(String filename) {
        throw new UnsupportedOperationException("load(Path) no está soportado en Supabase Storage. Use loadBytes().");
    }

    @Override
    public Resource loadAsResource(String filename) {
        throw new UnsupportedOperationException("loadAsResource() no está soportado en Supabase Storage. Use loadBytes().");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll() no está soportado en Supabase Storage por seguridad.");
    }

    // --- Utilidades privadas ---

    /**
     * Verifica si el bucket "documentos" existe en Supabase Storage.
     * Si no existe, lo crea como público.
     */
    private void crearBucketSiNoExiste() {
        try {
            // Verificar si el bucket existe
            String listUrl = supabaseUrl + "/storage/v1/bucket/" + BUCKET_NAME;
            HttpRequest checkRequest = HttpRequest.newBuilder()
                    .uri(URI.create(listUrl))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .GET()
                    .build();

            HttpResponse<String> checkResponse = httpClient.send(checkRequest, HttpResponse.BodyHandlers.ofString());

            if (checkResponse.statusCode() == 200) {
                System.out.println("Bucket '" + BUCKET_NAME + "' ya existe en Supabase Storage.");
                return;
            }

            // El bucket no existe, crearlo
            String createUrl = supabaseUrl + "/storage/v1/bucket";
            String createBody = "{\"id\":\"" + BUCKET_NAME + "\",\"name\":\"" + BUCKET_NAME + "\",\"public\":false}";

            HttpRequest createRequest = HttpRequest.newBuilder()
                    .uri(URI.create(createUrl))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createBody))
                    .build();

            HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

            if (createResponse.statusCode() >= 200 && createResponse.statusCode() < 300) {
                System.out.println("Bucket '" + BUCKET_NAME + "' creado en Supabase Storage.");
            } else {
                System.err.println("No se pudo crear el bucket: " + createResponse.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al verificar/crear bucket en Supabase Storage: " + e.getMessage());
        }
    }
}
