// Eliminar cuando se implemente la base de datos real

package com.proyectodam.fichApp_api.config;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.MetodoFichaje;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.model.Empresa;
import com.proyectodam.fichApp_api.model.Fichaje;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.repository.EmpresaRepository;
import com.proyectodam.fichApp_api.repository.FichajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FichajeRepository fichajeRepository;
    private final com.proyectodam.fichApp_api.repository.DocumentoRepository documentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("---- INICIANDO DATA SEEDER ----");

        // 1. Crear Empresa por defecto
        Empresa empresa = empresaRepository.findAll().stream().findFirst().orElse(null);
        if (empresa == null) {
            empresa = new Empresa();
            empresa.setNombre("FichApp Tech S.L.");
            empresa.setCif("B12345678");
            empresa.setDireccion("Calle Innovación 1, Madrid");
            empresa.setEmailContacto("admin@fichapp.com");
            empresa.setTelefono("910000000");
            empresa = empresaRepository.save(empresa);
            System.out.println("Empresa creada: " + empresa.getNombre());
        } else {
            System.out.println("Empresa ya existe: " + empresa.getNombre());
        }

        // 2. Crear Usuarios (Admin y Empleado)
        crearEmpleadoSiNoExiste(empresa, "admin@fichapp.com", "Admin", "User", "admin123", true);
        Empleado usuario = crearEmpleadoSiNoExiste(empresa, "user@fichapp.com", "Usuario", "Prueba", "user123", false);
        if (usuario != null) {
        }

        // 3. Crear Fichajes de prueba para el usuario
        if (fichajeRepository.count() == 0 && usuario != null) {
            crearFichajesPrueba(usuario);
        }

        // 4. Crear Documentos de prueba
        if (documentoRepository.count() == 0 && usuario != null) {
            crearDocumentosPrueba(usuario);
        }

        System.out.println("---- DATA SEEDER FINALIZADO ----");
    }

    private Empleado crearEmpleadoSiNoExiste(Empresa empresa, String email, String nombre, String apellidos,
            String password, boolean isAdmin) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByEmail(email);
        if (empleadoOpt.isPresent()) {
            System.out.println("Empleado ya existe: " + email);
            return empleadoOpt.get();
        }

        Empleado empleado = new Empleado();
        empleado.setEmpresa(empresa);
        empleado.setNombre(nombre);
        empleado.setApellidos(apellidos);
        empleado.setEmail(email);
        empleado.setPasswordHash(passwordEncoder.encode(password));
        empleado.setDniNie(isAdmin ? "00000000A" : "11111111B");
        empleado.setTelefono(isAdmin ? "600000000" : "600111222");
        empleado.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        empleado.setGenero(TipoGenero.M);
        empleado.setEstado(EstadoEmpleado.ACTIVO);
        empleado.setFechaAltaSistema(LocalDate.now());

        // TODO: Asignar ROL cuando el sistema de roles esté implementado
        // Por ahora, asumimos que todos tienen acceso básico o discriminamos por lógica

        return empleadoRepository.save(empleado);
    }

    private void crearFichajesPrueba(Empleado empleado) {
        // Ayer: Entrada 09:00, Salida 18:00
        LocalDateTime ayer = LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0);

        Fichaje entrada = new Fichaje();
        entrada.setEmpleado(empleado);
        entrada.setTimestampDispositivo(ayer);
        entrada.setTimestampServidor(ayer);
        entrada.setTipoEvento(TipoEventoFichaje.ENTRADA);
        entrada.setMetodoRegistro(MetodoFichaje.MANUAL);
        entrada.setEsValido(true);
        fichajeRepository.save(entrada);

        Fichaje salida = new Fichaje();
        salida.setEmpleado(empleado);
        salida.setTimestampDispositivo(ayer.plusHours(9));
        salida.setTimestampServidor(ayer.plusHours(9));
        salida.setTipoEvento(TipoEventoFichaje.SALIDA);
        salida.setMetodoRegistro(MetodoFichaje.MANUAL);
        salida.setEsValido(true);
        fichajeRepository.save(salida);

        // Hoy: Entrada 08:55
        LocalDateTime hoy = LocalDateTime.now().withHour(8).withMinute(55).withSecond(0);
        Fichaje entradaHoy = new Fichaje();
        entradaHoy.setEmpleado(empleado);
        entradaHoy.setTimestampDispositivo(hoy);
        entradaHoy.setTimestampServidor(hoy);
        entradaHoy.setTipoEvento(TipoEventoFichaje.ENTRADA);
        entradaHoy.setMetodoRegistro(MetodoFichaje.MANUAL);
        entradaHoy.setEsValido(true);
        fichajeRepository.save(entradaHoy);

        System.out.println("Fichajes de prueba creados para: " + empleado.getEmail());
    }

    private void crearDocumentosPrueba(Empleado empleado) {
        try {
            String fileName = "nomina_prueba_" + System.currentTimeMillis() + ".txt";
            String contenido = "Este es un documento de prueba generado automáticamente para " + empleado.getNombre();

            // Asegurar directorio uploads
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads").toAbsolutePath().normalize();
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }

            // Escribir archivo físico
            java.nio.file.Path targetLocation = uploadDir.resolve(fileName);
            java.nio.file.Files.write(targetLocation, contenido.getBytes());

            // Calcular Hash
            String hash = calcularHash(contenido.getBytes());

            // Crear entidad
            com.proyectodam.fichApp_api.model.Documento doc = com.proyectodam.fichApp_api.model.Documento.builder()
                    .nombreArchivo(fileName)
                    .rutaAcceso(targetLocation.toString())
                    .tipoMime("text/plain")
                    .tamanoBytes(contenido.length())
                    .categoria(com.proyectodam.fichApp_api.enums.CategoriaDocumento.NOMINA)
                    .estadoFirma(com.proyectodam.fichApp_api.enums.EstadoFirma.PENDIENTE)
                    .hashDocumento(hash)
                    .empleado(empleado)
                    .build();

            documentoRepository.save(doc);
            System.out.println("Documento de prueba creado para: " + empleado.getEmail());

        } catch (Exception e) {
            System.err.println("Error al crear documento de prueba: " + e.getMessage());
        }
    }

    private String calcularHash(byte[] content) throws java.security.NoSuchAlgorithmException {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
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
}
