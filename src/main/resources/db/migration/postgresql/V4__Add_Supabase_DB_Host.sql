-- V4: Añadir campo supa_db_host para configurar el host del Pooler de Supabase dinámicamente
ALTER TABLE "configuracion_conexion" ADD COLUMN IF NOT EXISTS "supa_db_host" VARCHAR(255) DEFAULT 'aws-0-eu-west-1.pooler.supabase.com';
