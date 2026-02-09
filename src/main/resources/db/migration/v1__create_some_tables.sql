CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE empleados (
    id_empleado SERIAL PRIMARY KEY,
    id_empresa INTEGER,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    pin_quiosco_hash VARCHAR(255),
    img_perfil_url VARCHAR(255),
    dni_nie VARCHAR(20),
    nuss VARCHAR(20),
    fecha_nacimiento DATE,
    genero VARCHAR(50),
    direccion VARCHAR(255),
    telefono VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    fecha_alta_sistema DATE NOT NULL,
    updated_at TIMESTAMP,
);

ALTER TABLE empleados
ADD CONSTRAINT fk_empleados_empresa
FOREIGN KEY (id_empresa)
REFERENCES empresas(id_empresa);

CREATE TABLE contratos (
  id_contrato SERIAL PRIMARY KEY,
  id_empleado INTEGER,
  id_horario INTEGER,
  id_departamento INTEGER,
  id_rol INTEGER,
  fecha_inicio DATE NOT NULL,
  fecha_fin DATE,
  tipo_contrato VARCHAR(50),
  horas_semanales TIME,
  created_at TIMESTAMP NOT NULL
);

ALTER TABLE contratos
ADD CONSTRAINT fk_contratos_empleado
FOREIGN KEY (id_empleado)
REFERENCES empleados(id_empleado);

ALTER TABLE contratos
ADD CONSTRAINT fk_contratos_rol
FOREIGN KEY (id_rol)
REFERENCES roles(id_rol);

ALTER TABLE contratos
ADD CONSTRAINT fk_contratos_departamento
FOREIGN KEY (id_departamento)
REFERENCES departamentos(id_departamento);

ALTER TABLE contratos
ADD CONSTRAINT fk_contratos_horario
FOREIGN KEY (id_horario)
REFERENCES horarios(id_horario);

CREATE TABLE departamentos (
  id_departamento SERIAL PRIMARY KEY,
  id_empresa INTEGER,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(500)
);

ALTER TABLE departamentos
ADD CONSTRAINT fk_departamentos_empresa
FOREIGN KEY (id_empresa)
REFERENCES empresas(id_empresa);

CREATE TABLE roles (
  id_rol SERIAL PRIMARY KEY,
  nombre VARCHAR(255) UNIQUE,
  permisos TEXT
);