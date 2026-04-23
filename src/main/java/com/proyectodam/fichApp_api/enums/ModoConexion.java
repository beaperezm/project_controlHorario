package com.proyectodam.fichApp_api.enums;

/**
 * Enumerado que define los modos de conexión disponibles para la aplicación.
 * Permite alternar entre entorno local, remoto personalizado (VPS/Servidor propio), 
 * entorno de pruebas con semilla o infraestructura cloud (Supabase).
 */
public enum ModoConexion {
    LOCAL("local", "http://localhost:8080"),
    REMOTO("server", null), // Antes SERVIDOR. URL null para obligar a configurar url_personalizada.
    REMOTESEED("remoteseed", null), // Perfil para pruebas con datos de semilla remotos
    SUPABASE("supabase", null);

    private final String perfilSpring;
    private final String urlDefecto;

    ModoConexion(String perfilSpring, String urlDefecto) {
        this.perfilSpring = perfilSpring;
        this.urlDefecto = urlDefecto;
    }

    public String getPerfilSpring() {
        return perfilSpring;
    }

    public String getUrlDefecto() {
        return urlDefecto;
    }

    /**
     * Obtiene el modo de conexión correspondiente al perfil de Spring activo.
     * Si no encuentra coincidencia, devuelve LOCAL por defecto.
     */
    public static ModoConexion fromPerfil(String perfil) {
        for (ModoConexion modo : values()) {
            if (modo.perfilSpring.equalsIgnoreCase(perfil)) {
                return modo;
            }
        }
        return LOCAL;
    }
}
