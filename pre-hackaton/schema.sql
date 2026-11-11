-- Base de datos para el CRUD de personas.
-- El contenedor ya crea la base db_tarea; este script es solo de referencia
-- o para ejecutarlo manualmente desde MySQL Workbench.

CREATE DATABASE IF NOT EXISTS db_tarea
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_tarea;

CREATE TABLE IF NOT EXISTS personas (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    dni              VARCHAR(8)  NOT NULL UNIQUE,
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    telefono         VARCHAR(15)  NULL,
    correo           VARCHAR(120) NULL,
    direccion        VARCHAR(200) NULL,
    fecha_nacimiento DATE         NULL,
    creado_en        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
