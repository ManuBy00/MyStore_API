-- Creación de la base de datos (si no existe por el docker-compose)
CREATE DATABASE IF NOT EXISTS myStoreAPI_db;
USE myStoreAPI_db;

-- 1. Tabla de Usuarios (Clientes)
-- Almacena credenciales y contacto para notificaciones [cite: 360]
CREATE TABLE clientes (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE, -- Unique para evitar registros duplicados
                          password VARCHAR(255) NOT NULL,    -- Encriptada por el backend
                          fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de empleados
-- Define al personal disponible para las citas
CREATE TABLE empleados (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          especialidad VARCHAR(50),
                          activo BOOLEAN DEFAULT TRUE
);

-- 3. Tabla de Servicios
-- Define los "packs" con su duración para el algoritmo de disponibilidad [cite: 329]
CREATE TABLE servicios (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           precio DECIMAL(10, 2) NOT NULL,    -- Precisión para facturación
                           duracion_minutos INT NOT NULL      -- Clave para validar solapamientos
);

-- 4. Tabla de Citas (Relación N:1 con las anteriores)
-- Centraliza la lógica de reservas y estados [cite: 151, 361]
CREATE TABLE citas (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       fecha_hora DATETIME NOT NULL,
                       estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA') DEFAULT 'PENDIENTE',
                       cliente_id INT NOT NULL,
                       empleado_id INT NOT NULL,
                       servicio_id INT NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones de Integridad Referencial
                       CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id)
                           REFERENCES clientes(id) ON DELETE CASCADE,
                       CONSTRAINT fk_cita_empleado FOREIGN KEY (empleado_id)
                           REFERENCES empleados(id) ON DELETE RESTRICT,
                       CONSTRAINT fk_cita_servicio FOREIGN KEY (servicio_id)
                           REFERENCES servicios(id) ON DELETE RESTRICT,

    -- Índice para optimizar las consultas de disponibilidad del algoritmo
                       INDEX idx_disponibilidad (empleado_id, fecha_hora)
);