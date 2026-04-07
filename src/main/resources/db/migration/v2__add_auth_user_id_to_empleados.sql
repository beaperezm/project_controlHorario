ALTER TABLE empleados
ADD COLUMN auth_user_id VARCHAR(255);

ALTER TABLE empleados
ADD CONSTRAINT unique_auth_user_id UNIQUE (auth_user_id);

ALTER TABLE empleados
ADD COLUMN activo_en_auth BOOLEAN DEFAULT false;