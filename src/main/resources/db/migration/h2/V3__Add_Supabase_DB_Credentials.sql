-- Migración para añadir soporte de credenciales de base de datos de Supabase en H2
ALTER TABLE "configuracion_conexion" ADD COLUMN "supa_db_pass" varchar(255);
ALTER TABLE "configuracion_conexion" ADD COLUMN "supa_db_user" varchar(100) DEFAULT 'postgres';
ALTER TABLE "configuracion_conexion" ADD COLUMN "supa_db_name" varchar(100) DEFAULT 'postgres';
