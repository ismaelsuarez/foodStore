-- Food Store - Base de datos del sistema
-- Este script crea las tablas principales y carga datos de prueba.

DROP DATABASE IF EXISTS pedidos_db;
CREATE DATABASE pedidos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pedidos_db;

DROP USER IF EXISTS 'foodstore_user'@'localhost';
CREATE USER 'foodstore_user'@'localhost' IDENTIFIED BY 'foodstore123';
GRANT ALL PRIVILEGES ON pedidos_db.* TO 'foodstore_user'@'localhost';

-- Tabla categoria
CREATE TABLE categoria (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    eliminado   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
);

-- Tabla producto
CREATE TABLE producto (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)   NOT NULL,
    precio       DECIMAL(12,2)  NOT NULL,
    descripcion  VARCHAR(255),
    stock        INT            NOT NULL DEFAULT 0,
    imagen       VARCHAR(255),
    disponible   BOOLEAN        NOT NULL DEFAULT TRUE,
    categoria_id BIGINT         NOT NULL,
    eliminado    BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_stock  CHECK (stock >= 0)
);

-- Tabla usuario
CREATE TABLE usuario (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    mail        VARCHAR(150) NOT NULL,
    celular     VARCHAR(50),
    contrasenia VARCHAR(100),
    rol         VARCHAR(20)  NOT NULL DEFAULT 'USUARIO',
    eliminado   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuario_mail UNIQUE (mail)
);

-- Tabla pedido
CREATE TABLE pedido (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha      DATE          NOT NULL,
    estado     VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    total      DECIMAL(12,2) NOT NULL DEFAULT 0,
    forma_pago VARCHAR(20)   NOT NULL,
    usuario_id BIGINT        NOT NULL,
    eliminado  BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Tabla detalle_pedido
CREATE TABLE detalle_pedido (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cantidad    INT           NOT NULL,
    subtotal    DECIMAL(12,2) NOT NULL DEFAULT 0,
    pedido_id   BIGINT        NOT NULL,
    producto_id BIGINT        NOT NULL,
    eliminado   BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detalle_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedido (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0)
);

-- Datos de prueba
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Lácteos',   'Productos derivados de la leche'),
    ('Panadería', 'Panes, facturas y galletas'),
    ('Bebidas',   'Gaseosas, jugos y aguas');

INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id) VALUES
    ('Leche Entera 1L', 1500.00, 'Leche de vaca',        50, 'leche.png',      TRUE, 1),
    ('Queso Cremoso',   4500.00, 'Queso por kg',         10, 'queso.png',      TRUE, 1),
    ('Pan Francés',     2000.00, 'Pan del día por kg',   30, 'pan.png',        TRUE, 2),
    ('Medialunas x12',  3500.00, 'Docena de manteca',     8, 'medialunas.png', TRUE, 2),
    ('Gaseosa Cola 2L', 2800.00, 'Bebida gaseosa',       40, 'cola.png',       TRUE, 3);

INSERT INTO usuario (nombre, apellido, mail, celular, contrasenia, rol) VALUES
    ('Admin', 'Sistema', 'admin@foodstore.com', '2610000000', '1234', 'ADMIN'),
    ('Juan',  'Pérez',   'juan.perez@mail.com', '2611111111', 'abcd', 'USUARIO'),
    ('María', 'Gómez',   'maria.gomez@mail.com','2612222222', 'abcd', 'USUARIO');

INSERT INTO pedido (fecha, estado, total, forma_pago, usuario_id) VALUES
    (CURRENT_DATE, 'PENDIENTE', 0, 'EFECTIVO', 2);

INSERT INTO detalle_pedido (cantidad, subtotal, pedido_id, producto_id) VALUES
    (2, 3000.00, 1, 1),
    (1, 4500.00, 1, 2);

UPDATE pedido SET total = (
    SELECT COALESCE(SUM(subtotal), 0) FROM detalle_pedido WHERE pedido_id = 1
) WHERE id = 1;

FLUSH PRIVILEGES;
