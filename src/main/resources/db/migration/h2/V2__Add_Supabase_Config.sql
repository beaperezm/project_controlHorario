-- Migración para añadir soporte de credenciales dinámicas de Supabase en H2
ALTER TABLE "configuracion_conexion" ADD COLUMN "supa_url" varchar(255);
ALTER TABLE "configuracion_conexion" ADD COLUMN "supa_key" varchar(500);
