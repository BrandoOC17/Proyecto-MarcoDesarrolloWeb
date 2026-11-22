-- ============================================================
-- Infinity Station - Migración para Spring Security (roles)
-- Úsalo si YA tienes la base infinity_station con datos.
-- Ejecutar UNA sola vez (en MySQL Workbench o consola mysql).
-- ============================================================
USE infinity_station;

-- 1) Columna de rol: todos los usuarios existentes pasan a CLIENTE.
ALTER TABLE usuario
  ADD COLUMN rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE' AFTER `contraseña`,
  ADD CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'CLIENTE'));

-- 2) Cuenta del administrador (guardada en la base con la clave cifrada con BCrypt).
--    Si el correo ya existía como cliente, se convierte en administrador.
INSERT INTO usuario (nombre, correo, `contraseña`, rol)
VALUES ('Administrador Infinity', 'adminInfinity@gmail.com', '$2b$10$SXevuK26PZDPVT3ew4ualOqqKv7uyhKECjeMblWYLo7HDi8rbgSBy', 'ADMINISTRADOR') AS nuevo
ON DUPLICATE KEY UPDATE rol = nuevo.rol, `contraseña` = nuevo.`contraseña`;

-- Verificación
SELECT id, nombre, correo, rol FROM usuario ORDER BY rol, id;
