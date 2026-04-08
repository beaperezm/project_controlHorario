package com.proyectodam.fichApp_api.enums;

/**
 * Enumerado que define los modos de conexión disponibles para la aplicación.
 * Permite alternar entre entorno local, servidor de pruebas y entorno en la
 * nube.
 */
public enum ModoConexion {
    LOCAL("local", "http://localhost:8080"),
    SERVIDOR("server", "https://fichapp.duckdns.org"),
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
        return LOCAL; // Por defecto ante cualquier duda, mejor no conectar a producción
    }
}
