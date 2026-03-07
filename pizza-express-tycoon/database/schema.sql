CREATE DATABASE IF NOT EXISTS pizza_express_tycoon;
USE pizza_express_tycoon;

-- =========================================
-- TABLA: roles
-- =========================================
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

-- =========================================
-- TABLA: sucursales
-- =========================================
CREATE TABLE sucursales (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================
-- TABLA: usuarios
-- =========================================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    id_rol INT NOT NULL,
    id_sucursal INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES roles(id_rol),
    CONSTRAINT fk_usuario_sucursal
        FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
);

-- =========================================
-- TABLA: productos
-- =========================================
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_sucursal INT NOT NULL,
    CONSTRAINT fk_producto_sucursal
        FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
);

-- =========================================
-- TABLA: niveles
-- =========================================
CREATE TABLE niveles (
    id_nivel INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    tiempo_base_segundos INT NOT NULL,
    puntaje_minimo INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================
-- TABLA: partidas
-- =========================================
CREATE TABLE partidas (
    id_partida INT AUTO_INCREMENT PRIMARY KEY,
    id_jugador INT NOT NULL,
    id_sucursal INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NULL,
    tiempo_total_segundos INT NOT NULL,
    estado_partida VARCHAR(20) NOT NULL,
    CONSTRAINT fk_partida_jugador
        FOREIGN KEY (id_jugador) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_partida_sucursal
        FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal),
    CONSTRAINT chk_estado_partida
        CHECK (estado_partida IN ('EN_CURSO', 'FINALIZADA'))
);

-- =========================================
-- TABLA: puntajes
-- =========================================
CREATE TABLE puntajes (
    id_puntaje INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT NOT NULL UNIQUE,
    puntos_totales INT NOT NULL DEFAULT 0,
    pedidos_entregados INT NOT NULL DEFAULT 0,
    pedidos_cancelados INT NOT NULL DEFAULT 0,
    pedidos_no_entregados INT NOT NULL DEFAULT 0,
    bonificaciones_rapidas INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_puntaje_partida
        FOREIGN KEY (id_partida) REFERENCES partidas(id_partida)
);

-- =========================================
-- TABLA: pedidos
-- =========================================
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT NOT NULL,
    estado_actual VARCHAR(20) NOT NULL,
    tiempo_limite_segundos INT NOT NULL,
    tiempo_restante_segundos INT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_finalizacion DATETIME NULL,
    CONSTRAINT fk_pedido_partida
        FOREIGN KEY (id_partida) REFERENCES partidas(id_partida),
    CONSTRAINT chk_estado_pedido
        CHECK (estado_actual IN (
            'RECIBIDA',
            'PREPARANDO',
            'EN_HORNO',
            'ENTREGADA',
            'CANCELADA',
            'NO_ENTREGADO'
        ))
);

-- =========================================
-- TABLA: detalle_pedido
-- =========================================
CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT chk_cantidad_positiva
        CHECK (cantidad > 0)
);

-- =========================================
-- TABLA: historial_estados
-- =========================================
CREATE TABLE historial_estados (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    estado_anterior VARCHAR(20) NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    fecha_cambio DATETIME NOT NULL,
    CONSTRAINT fk_historial_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT chk_estado_anterior
        CHECK (
            estado_anterior IS NULL OR estado_anterior IN (
                'RECIBIDA',
                'PREPARANDO',
                'EN_HORNO',
                'ENTREGADA',
                'CANCELADA',
                'NO_ENTREGADO'
            )
        ),
    CONSTRAINT chk_estado_nuevo
        CHECK (
            estado_nuevo IN (
                'RECIBIDA',
                'PREPARANDO',
                'EN_HORNO',
                'ENTREGADA',
                'CANCELADA',
                'NO_ENTREGADO'
            )
        )
);

-- =========================================
-- TABLA: niveles_alcanzados
-- =========================================
CREATE TABLE niveles_alcanzados (
    id_nivel_alcanzado INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT NOT NULL UNIQUE,
    id_nivel INT NOT NULL,
    fecha_registro DATETIME NOT NULL,
    CONSTRAINT fk_nivel_alcanzado_partida
        FOREIGN KEY (id_partida) REFERENCES partidas(id_partida),
    CONSTRAINT fk_nivel_alcanzado_nivel
        FOREIGN KEY (id_nivel) REFERENCES niveles(id_nivel)
);

-- =========================================
-- DATOS INICIALES
-- =========================================

INSERT INTO roles (nombre, descripcion) VALUES
('JUGADOR', 'Usuario que juega partidas'),
('ADMIN_TIENDA', 'Usuario que administra productos y reportes de su sucursal'),
('SUPER_ADMIN', 'Usuario con acceso global al sistema');

INSERT INTO sucursales (nombre, direccion, activa) VALUES
('Sucursal Centro', 'Zona 1, Centro', TRUE),
('Sucursal Norte', 'Zona 5, Norte', TRUE);

INSERT INTO niveles (nombre, tiempo_base_segundos, puntaje_minimo, activo) VALUES
('Nivel 1', 60, 0, TRUE),
('Nivel 2', 50, 500, TRUE),
('Nivel 3', 40, 1000, TRUE);

-- Usuarios de ejemplo
INSERT INTO usuarios (nombre_usuario, contrasena, nombre_completo, id_rol, id_sucursal, activo) VALUES
('jugador1', '1234', 'Jugador Demo', 1, 1, TRUE),
('admin1', '1234', 'Admin Tienda Demo', 2, 1, TRUE),
('super1', '1234', 'Super Admin Demo', 3, 1, TRUE);

-- Productos de ejemplo
INSERT INTO productos (nombre, descripcion, precio, activo, id_sucursal) VALUES
('Pizza Pepperoni', 'Pizza clásica con pepperoni', 75.00, TRUE, 1),
('Pizza Hawaiana', 'Pizza con jamón y piña', 80.00, TRUE, 1),
('Pizza Suprema', 'Pizza con varios ingredientes', 95.00, TRUE, 1),
('Pizza Queso', 'Pizza solo queso', 65.00, TRUE, 2);