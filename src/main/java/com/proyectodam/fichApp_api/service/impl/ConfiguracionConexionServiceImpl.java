package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import com.proyectodam.fichApp_api.repository.ConfiguracionConexionRepository;
import com.proyectodam.fichApp_api.service.IConfiguracionConexionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionConexionServiceImpl implements IConfiguracionConexionService {

    @Autowired
    private ConfiguracionConexionRepository repository;

    @Value("${spring.profiles.active:local}")
    private String perfilActivo;

    @PostConstruct
    public void init() {
        inicializarConfiguracion();
    }

    @Override
    @Transactional
    public void inicializarConfiguracion() {
        ConfiguracionConexion config = obtenerConfiguracion();
        
        System.out.println(" FichApp Backend arrancando en modo: " + config.getModo());
        
        // Se desactiva la generación automática al arrancar para permitir al usuario "escapar" de una configuración
        // errónea borrando manualmente el archivo .env. Los archivos se regenerarán al guardar desde la interfaz.
        /*
        if (config.getModo() == ModoConexion.SUPABASE && config.getSupaUrl() != null && !config.getSupaUrl().isEmpty()) {
            System.out.println(" URL de Supabase configurada: " + config.getSupaUrl());
            escribirArchivoOverride(config);
        } else if (config.getModo() == ModoConexion.SUPABASE) {
            System.out.println(" ADVERTENCIA: Modo Supabase activo pero falta la URL en la base de datos.");
        }
        */
    }

    @Override
    public ConfiguracionConexion obtenerConfiguracion() {
        ConfiguracionConexion config = repository.obtenerConfiguracion()
                .orElseGet(() -> {
                    ConfiguracionConexion nueva = new ConfiguracionConexion();
                    nueva.setId(1L);
                    nueva.setModo(ModoConexion.LOCAL);
                    return repository.save(nueva);
                });

        // Lógica de respaldo: Si estamos en modo SUPABASE pero faltan datos críticos,
        // intentamos leerlos del archivo config.properties (si existe)
        if (config.getModo() == ModoConexion.SUPABASE && (config.getSupaUrl() == null || config.getSupaUrl().isEmpty())) {
            try {
                java.nio.file.Path propPath = java.nio.file.Paths.get("config", "config.properties");
                if (java.nio.file.Files.exists(propPath)) {
                    java.util.Properties props = new java.util.Properties();
                    try (java.io.InputStream is = java.nio.file.Files.newInputStream(propPath)) {
                        props.load(is);
                        String url = props.getProperty("supa.url");
                        String key = props.getProperty("supa.key");
                        if (url != null && !url.isEmpty()) {
                            config.setSupaUrl(url);
                            config.setSupaKey(key);
                            config.setSupaDbPass(props.getProperty("supa.db.pass"));
                            config.setSupaDbName(props.getProperty("supa.db.name"));
                            config.setSupaDbUser(props.getProperty("supa.db.user"));
                            config.setSupaDbHost(props.getProperty("supa.db.host"));
                            System.out.println("Configuración de Supabase cargada desde archivo (fallback)");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("No se pudo leer el archivo de respaldo: " + e.getMessage());
            }
        }
        
        return config;
    }

    @Override
    @Transactional
    // public ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada) {
    // public ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada, String supaUrl, String supaKey) {
    public ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada, String supaUrl, String supaKey, String supaDbPass, String supaDbUser, String supaDbName, String supaDbHost) {
        ConfiguracionConexion config = obtenerConfiguracion();
        boolean cambiaModo = !config.getModo().equals(nuevoModo);
        config.setModo(nuevoModo);
        config.setUrlPersonalizada(urlPersonalizada);
        config.setSupaUrl(supaUrl);
        config.setSupaKey(supaKey);
        config.setSupaDbPass(supaDbPass);
        config.setSupaDbUser(supaDbUser != null ? supaDbUser : "postgres");
        config.setSupaDbName(supaDbName != null ? supaDbName : "postgres");
        config.setSupaDbHost(supaDbHost != null && !supaDbHost.isEmpty() ? supaDbHost : "aws-0-eu-west-1.pooler.supabase.com");
        config.setRequiereReinicio(cambiaModo);
        ConfiguracionConexion saved = repository.save(config);

        // Generar el archivo de override para que al reiniciar se use la BD correcta
        escribirArchivoOverride(saved);

        return saved;
    }

    @Override
    public java.util.Map<String, Object> inicializarBaseDatosSupabase() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        ConfiguracionConexion config = obtenerConfiguracion();

        if (config.getSupaUrl() == null || config.getSupaUrl().isEmpty() || 
            config.getSupaDbPass() == null || config.getSupaDbPass().isEmpty()) {
            result.put("success", false);
            result.put("message", "Faltan datos de configuración (URL o Contraseña de DB)");
            return result;
        }

        try {
            // Extraer el project ref de la URL del proyecto
            // URL: https://[PROYECTO].supabase.co
            String supaUrl = config.getSupaUrl().replace("https://", "").replace("http://", "");
            String projectRef = supaUrl.split("\\.")[0];

            // Usar el Supabase Connection Pooler (IPv4, accesible desde cualquier red)
            // El host directo (db.[ref].supabase.co) solo tiene IPv6 y no es accesible en muchas redes
            String poolerHost = config.getSupaDbHost() != null && !config.getSupaDbHost().isEmpty()
                    ? config.getSupaDbHost()
                    : "aws-0-eu-west-1.pooler.supabase.com";
            // El usuario del pooler lleva el project ref: postgres.[projectRef]
            String poolerUser = config.getSupaDbUser() + "." + projectRef;
            String jdbcUrl = String.format("jdbc:postgresql://%s:6543/%s?sslmode=require", poolerHost, config.getSupaDbName());

            org.flywaydb.core.Flyway flyway = org.flywaydb.core.Flyway.configure()
                    .dataSource(jdbcUrl, poolerUser, config.getSupaDbPass())
                    .locations("classpath:db/migration/postgresql")
                    .load();

            org.flywaydb.core.api.output.MigrateResult migrateResult = flyway.migrate();

            result.put("success", true);
            result.put("message", "Base de datos inicializada correctamente");
            result.put("migrationsExecuted", migrateResult.migrationsExecuted);
            result.put("targetDatabase", poolerHost);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public ModoConexion obtenerModoActual() {
        return obtenerConfiguracion().getModo();
    }

    @Override
    public String obtenerUrlBase() {
        ConfiguracionConexion config = obtenerConfiguracion();
        if (config.getUrlPersonalizada() != null && !config.getUrlPersonalizada().isEmpty()) {
            return config.getUrlPersonalizada();
        }
        return config.getModo().getUrlDefecto();
    }

    /**
     * Genera un script de arranque que configura el perfil y la fuente de datos
     * mediante argumentos de línea de comandos (la forma correcta en Spring Boot 2.4+).
     * También escribe un archivo .env legible con las variables.
     */
    private void escribirArchivoOverride(ConfiguracionConexion config) {
        try {
            java.nio.file.Path configDir = java.nio.file.Paths.get("config");
            if (!java.nio.file.Files.exists(configDir)) {
                java.nio.file.Files.createDirectories(configDir);
            }

            StringBuilder envContent = new StringBuilder();
            envContent.append("# Generado automáticamente por FichApp - NO EDITAR MANUALMENTE\n");
            envContent.append("# Modo: ").append(config.getModo()).append("\n\n");

            if (config.getModo() == ModoConexion.SUPABASE) {
                if (config.getSupaUrl() == null || config.getSupaUrl().isEmpty()) {
                    System.out.println("ADVERTENCIA: No se puede generar .env completo porque falta la URL de Supabase.");
                    envContent.append("# CONFIGURACION INCOMPLETA: Falta supaUrl\n");
                } else {
                    String supaUrl = config.getSupaUrl().toLowerCase()
                            .replace("https://", "")
                            .replace("http://", "")
                            .split("/")[0]; // Quitar posibles rutas finales

                    String projectRef = supaUrl.split("\\.")[0];
                    // Si por error empieza por www. lo quitamos
                    if (projectRef.equals("www") && supaUrl.split("\\.").length > 1) {
                        projectRef = supaUrl.split("\\.")[1];
                    }
                    String poolerHost = config.getSupaDbHost() != null && !config.getSupaDbHost().isEmpty()
                            ? config.getSupaDbHost()
                            : "aws-0-eu-west-1.pooler.supabase.com";
                    String poolerUser = config.getSupaDbUser() + "." + projectRef;

                    envContent.append("SPRING_PROFILES_ACTIVE=supabase\n");
                    envContent.append("SUPABASE_URL=").append(
                            String.format("jdbc:postgresql://%s:6543/%s?sslmode=require&prepareThreshold=0", poolerHost, config.getSupaDbName())).append("\n");
                    envContent.append("SUPABASE_USER=").append(poolerUser).append("\n");
                    envContent.append("SUPABASE_PASS=").append(config.getSupaDbPass()).append("\n");
                    envContent.append("SUPABASE_PROJECT_URL=").append(config.getSupaUrl()).append("\n");
                    envContent.append("SUPABASE_ANON_KEY=").append(config.getSupaKey()).append("\n");
                }
            } else {
                envContent.append("SPRING_PROFILES_ACTIVE=").append(config.getModo().getPerfilSpring()).append("\n");
            }

            // Escribir archivo .env
            java.nio.file.Path envPath = configDir.resolve("fichapp.env");
            java.nio.file.Files.writeString(envPath, envContent.toString());
            
            System.out.println("======================================================");
            System.out.println("CONFIGURACION EXPORTADA EXITOSAMENTE");
            System.out.println("Archivo: " + envPath.toAbsolutePath());
            System.out.println("Modo: " + config.getModo());
            System.out.println("======================================================");

            // TAMBIÉN ACTUALIZAR config.properties (para el Frontend)
            StringBuilder propContent = new StringBuilder();
            propContent.append("#Configuracion de conexion FichApp\n");
            propContent.append("#").append(new java.util.Date()).append("\n");
            propContent.append("modo.conexion=").append(config.getModo()).append("\n");
            propContent.append("server.port=8081\n");
            propContent.append("server.db=fichapp_db\n");
            
            if (config.getModo() == ModoConexion.SUPABASE) {
                propContent.append("supa.url=").append(config.getSupaUrl()).append("\n");
                propContent.append("supa.key=").append(config.getSupaKey()).append("\n");
                propContent.append("supa.db.host=").append(config.getSupaDbHost()).append("\n");
                propContent.append("supa.db.user=").append(config.getSupaDbUser()).append("\n");
                propContent.append("supa.db.pass=").append(config.getSupaDbPass()).append("\n");
                propContent.append("supa.db.name=").append(config.getSupaDbName()).append("\n");
            }

            java.nio.file.Path propPath = configDir.resolve("config.properties");
            java.nio.file.Files.writeString(propPath, propContent.toString());
            System.out.println("Frontend sincronizado: " + propPath.toAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("ERROR CRITICO al escribir archivo de configuracion: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            java.nio.file.Path configDir = java.nio.file.Paths.get("config");
            
            // Generar script de arranque Windows (.bat)
            StringBuilder batContent = new StringBuilder();
            batContent.append("@echo off\n");
            batContent.append("REM Generado automáticamente por FichApp\n");
            if (config.getModo() == ModoConexion.SUPABASE) {
                String supaUrl = config.getSupaUrl().toLowerCase()
                        .replace("https://", "")
                        .replace("http://", "")
                        .split("/")[0]; // Quitar posibles rutas finales
                
                String projectRef = supaUrl.split("\\.")[0];
                // Si por error empieza por www. lo quitamos
                if (projectRef.equals("www") && supaUrl.split("\\.").length > 1) {
                    projectRef = supaUrl.split("\\.")[1];
                }
                
                String poolerHost = config.getSupaDbHost() != null && !config.getSupaDbHost().isEmpty()
                        ? config.getSupaDbHost()
                        : "aws-0-eu-west-1.pooler.supabase.com";
                String poolerUser = config.getSupaDbUser() + "." + projectRef;

                batContent.append("set SPRING_PROFILES_ACTIVE=supabase\n");
                batContent.append("set SUPABASE_URL=jdbc:postgresql://").append(poolerHost)
                        .append(":6543/").append(config.getSupaDbName()).append("?sslmode=require&prepareThreshold=0\n");
                batContent.append("set SUPABASE_USER=").append(poolerUser).append("\n");
                batContent.append("set SUPABASE_PASS=").append(config.getSupaDbPass()).append("\n");
                batContent.append("set SUPABASE_PROJECT_URL=").append(config.getSupaUrl()).append("\n");
                batContent.append("set SUPABASE_ANON_KEY=").append(config.getSupaKey()).append("\n");
            } else {
                batContent.append("set SPRING_PROFILES_ACTIVE=").append(config.getModo().getPerfilSpring()).append("\n");
            }
            batContent.append("echo Arrancando FichApp en modo: ").append(config.getModo()).append("\n");
            batContent.append("java -jar fichApp-api.jar\n");
            batContent.append("pause\n");

            java.nio.file.Path batPath = configDir.resolve("start-backend.bat");
            java.nio.file.Files.writeString(batPath, batContent.toString());

            System.out.println("Archivos de configuración generados en config/ para modo: " + config.getModo());
        } catch (Exception e) {
            System.err.println("Error al escribir configuración de arranque: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
