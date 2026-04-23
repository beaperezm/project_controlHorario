package com.proyectodam.fichApp_api.config;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.model.Departamento;
import com.proyectodam.fichApp_api.model.Empresa;
import com.proyectodam.fichApp_api.model.Rol;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.repository.EmpresaRepository;
import com.proyectodam.fichApp_api.repository.ContratoRepository;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Horario;
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
    private final com.proyectodam.fichApp_api.repository.RolRepository rolRepository;
    private final com.proyectodam.fichApp_api.repository.DepartamentoRepository departamentoRepository;
    private final com.proyectodam.fichApp_api.repository.HorarioRepository horarioRepository;
    private final ContratoRepository contratoRepository;
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
        Rol adminRol = rolRepository.findByNombre("ADMIN").orElseThrow();
        Rol userRol = rolRepository.findByNombre("USER").orElseThrow();
        Departamento deptoDev = departamentoRepository.findAll().stream()
                .filter(d -> d.getNombre().equalsIgnoreCase("Desarrollo"))
                .findFirst().orElseThrow();
        Horario horarioGral = horarioRepository.findAll().stream().findFirst().orElseThrow();

        crearEmpleadoSiNoExiste(empresa, "admin@fichapp.com", "Admin", "User", "admin123", true,
                adminRol, deptoDev, horarioGral);
        crearEmpleadoSiNoExiste(empresa, "user@fichapp.com", "Usuario", "Prueba", "user123", false,
                userRol, deptoDev, horarioGral);

        
        // 4. Asegurar que todos los empleados tengan el PIN por defecto "0000"
        empleadoRepository.findAll().forEach(e -> {
            if (e.getPinQuioscoHash() == null || e.getPinQuioscoHash().isEmpty()) {
                e.setPinQuioscoHash(passwordEncoder.encode("0000"));
                empleadoRepository.save(e);
            }
        });
    }

    private Empleado crearEmpleadoSiNoExiste(Empresa empresa, String email, String nombre, String apellidos,
            String password, boolean isAdmin, Rol rol, Departamento depto, Horario horario) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByEmail(email);
        Empleado empleado;
        if (empleadoOpt.isPresent()) {
            empleado = empleadoOpt.get();
        } else {
            empleado = new Empleado();
            empleado.setEmpresa(empresa);
            empleado.setNombre(nombre);
            empleado.setApellidos(apellidos);
            empleado.setEmail(email);
            empleado.setPasswordHash(passwordEncoder.encode(password));
            empleado.setPinQuioscoHash(passwordEncoder.encode("0000"));
            empleado.setDniNie(isAdmin ? "00000000A" : "11111111B");
            empleado.setTelefono(isAdmin ? "600000000" : "600111222");
            empleado.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            empleado.setGenero(TipoGenero.M);
            empleado.setEstado(EstadoEmpleado.ACTIVO);
            empleado.setFechaAltaSistema(LocalDate.now());
            empleado.setDireccion("Calle de prueba 123");
            empleado = empleadoRepository.save(empleado);
        }

        final Empleado finalEmpleado = empleado;
        // Crear contrato si no tiene ninguno activo
        if (contratoRepository.findAll().stream().noneMatch(c -> c.getEmpleado().getIdEmpleado() == finalEmpleado.getIdEmpleado())) {
            Contrato contrato = new Contrato();
            contrato.setEmpleado(finalEmpleado);
            contrato.setRol(rol);
            contrato.setDepartamento(depto);
            contrato.setHorario(horario);
            contrato.setFechaInicio(LocalDate.now());
            contrato.setTipoContrato("INDEFINIDO");
            contrato.setCreatedAt(LocalDateTime.now());
            contratoRepository.save(contrato);
        }

        return empleado;
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
}
