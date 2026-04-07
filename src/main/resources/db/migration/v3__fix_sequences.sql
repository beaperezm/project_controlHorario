-- arreglando secuencias para que no de error por id repetido

SELECT setval(
    pg_get_serial_sequence('empleados', 'id_empleado'),
    COALESCE(SELECT MAX(id_empleado) FROM empleados), 1), true
);

SELECT setval(
    pg_get_serial_sequence('contratos', 'id_contrato'),
    COALESCE(SELECT MAX(id_contrato) FROM contratos), 1), true
);

SELECT setval(
    pg_get_serial_sequence('fichajes', 'id_fichaje'),
    COALESCE(SELECT MAX(id_fichaje) FROM fichajes), 1), true
);

SELECT setval(
    pg_get_serial_sequence('bolsa_horas', 'id_bolsa'),
    COALESCE(SELECT MAX(id_bolsa) FROM bolsa_horas), 1), true
);

SELECT setval(
    pg_get_serial_sequence('departamentos', 'id_departamento'),
    COALESCE(SELECT MAX(id_departamento) FROM departamentos), 1), true
);

SELECT setval(
    pg_get_serial_sequence('empresas', 'id_empresa'),
    COALESCE(SELECT MAX(id_empresa) FROM empresas), 1), true
);

SELECT setval(
    pg_get_serial_sequence('horarios', 'id_horario'),
    COALESCE(SELECT MAX(id_horario) FROM horarios), 1), true
);

SELECT setval(
    pg_get_serial_sequence('roles', 'id_rol'),
    COALESCE(SELECT MAX(id_rol) FROM roles), 1), true
);