// TODO: Eliminar esta clase una vez que la base de datos real esté operativa.

package com.proyectodam.fichApp_api.config;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.MetodoFichaje;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.model.Departamento;
import com.proyectodam.fichApp_api.model.Empresa;
import com.proyectodam.fichApp_api.model.Fichaje;
import com.proyectodam.fichApp_api.model.Rol;
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
    private final com.proyectodam.fichApp_api.repository.RolRepository rolRepository;
    private final com.proyectodam.fichApp_api.repository.DepartamentoRepository departamentoRepository;
    private final com.proyectodam.fichApp_api.repository.HorarioRepository horarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Verificar/crear empresa por defecto
        Empresa empresa = empresaRepository.findAll().stream().findFirst().orElse(null);
        if (empresa == null) {
            empresa = new Empresa();
            empresa.setNombre("FichApp Tech S.L.");
            empresa.setCif("B12345678");
            empresa.setDireccion("Calle Innovación 1, Madrid");
            empresa.setEmailContacto("admin@fichapp.com");
            empresa.setTelefono("910000000");
            empresa = empresaRepository.save(empresa);
        }

        // 2. Crear horario, roles y departamentos por defecto
        if (horarioRepository.count() == 0) {
            crearHorarioPorDefecto(empresa);
        }

        crearRolesPorDefecto();
        crearDepartamentosPorDefecto(empresa);

        // 3. Crear usuarios y datos de prueba
        crearEmpleadoSiNoExiste(empresa, "admin@fichapp.com", "Admin", "User", "admin123", true);
        Empleado usuario = crearEmpleadoSiNoExiste(empresa, "user@fichapp.com", "Usuario", "Prueba", "user123", false);

        if (usuario != null) {
            if (fichajeRepository.count() == 0) {
                crearFichajesPrueba(usuario);
            }
            if (documentoRepository.count() == 0) {
                crearDocumentosPrueba(usuario);
            }
        }
    }

    private Empleado crearEmpleadoSiNoExiste(Empresa empresa, String email, String nombre, String apellidos,
            String password, boolean isAdmin) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByEmail(email);
        if (empleadoOpt.isPresent()) {
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

        // TODO: Asignar el rol real
        return empleadoRepository.save(empleado);
    }

    private void crearFichajesPrueba(Empleado empleado) {
        // Fichajes de ayer (09:00 a 18:00)
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

        // Fichaje de hoy (08:55)
        LocalDateTime hoy = LocalDateTime.now().withHour(8).withMinute(55).withSecond(0);
        Fichaje entradaHoy = new Fichaje();
        entradaHoy.setEmpleado(empleado);
        entradaHoy.setTimestampDispositivo(hoy);
        entradaHoy.setTimestampServidor(hoy);
        entradaHoy.setTipoEvento(TipoEventoFichaje.ENTRADA);
        entradaHoy.setMetodoRegistro(MetodoFichaje.MANUAL);
        entradaHoy.setEsValido(true);
        fichajeRepository.save(entradaHoy);
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
                    .categoria("NÓMINA")
                    .estadoFirma(com.proyectodam.fichApp_api.enums.EstadoFirma.PENDIENTE)
                    .hashDocumento(hash)
                    .empleado(empleado)
                    .build();

            documentoRepository.save(doc);
        } catch (Exception e) {
        }
    }

    private void crearRolesPorDefecto() {
        String[] roles = { "ADMIN", "USER", "MANAGER", "RRHH" };
        for (String rolNombre : roles) {
            try {
                boolean existe = rolRepository.findAll().stream()
                        .anyMatch(r -> r.getNombre().equalsIgnoreCase(rolNombre));

                if (!existe) {
                    Rol rol = new Rol();
                    rol.setNombre(rolNombre);
                    rol.setPermisos("ALL");
                    rolRepository.save(rol);
                }
            } catch (Exception e) {
            }
        }
    }

    private void crearDepartamentosPorDefecto(Empresa empresa) {
        String[] deptos = { "Desarrollo", "Recursos Humanos", "Ventas", "Marketing", "Soporte" };
        for (String deptoNombre : deptos) {
            try {
                boolean existe = departamentoRepository.findAll().stream()
                        .anyMatch(d -> d.getNombre().equalsIgnoreCase(deptoNombre) &&
                                d.getEmpresa().getIdEmpresa() == empresa.getIdEmpresa());

                if (!existe) {
                    Departamento depto = new Departamento();
                    depto.setNombre(deptoNombre);
                    depto.setDescripcion("Departamento de " + deptoNombre);
                    depto.setEmpresa(empresa);
                    depto.setEmpresa(empresa);
                    departamentoRepository.save(depto);
                }
            } catch (Exception e) {
            }
        }
    }

    private void crearHorarioPorDefecto(Empresa empresa) {
        try {
            com.proyectodam.fichApp_api.model.Horario horario = new com.proyectodam.fichApp_api.model.Horario();
            horario.setNombre("Horario General");
            horario.setEmpresa(empresa);
            horario.setMargenFlexibleMin(15);
            // Configuración semanal
            String configSemanal = "[{\"dia\":\"LUNES\",\"entrada\":\"09:00\",\"salida\":\"18:00\"},{\"dia\":\"MARTES\",\"entrada\":\"09:00\",\"salida\":\"18:00\"},{\"dia\":\"MIERCOLES\",\"entrada\":\"09:00\",\"salida\":\"18:00\"},{\"dia\":\"JUEVES\",\"entrada\":\"09:00\",\"salida\":\"18:00\"},{\"dia\":\"VIERNES\",\"entrada\":\"09:00\",\"salida\":\"15:00\"}]";
            horario.setConfiguracionSemanal(configSemanal);

            horarioRepository.save(horario);
        } catch (Exception e) {
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
