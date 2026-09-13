-- =====================================================================
-- PROYECTO: STOCKLY - Sistema de Gestión de Inventarios para Tienda de Ropa
-- EVIDENCIA: GA6-220501096-AA2-EV03 - Script bases de datos del proyecto
-- SCRIPT: Modelo físico completo (estructura + índices + integridad
--          referencial + automatización de stock + vistas de consulta)
-- MOTOR: MySQL 8.0 (exportado desde MySQL Workbench)
-- =====================================================================
-- Este script corresponde al modelo físico trabajado en MySQL Workbench
-- a partir de la estructura definida en la evidencia EV02. Conserva las
-- 12 tablas, llaves y restricciones ya validadas, y agrega los objetos
-- adicionales que requiere el funcionamiento real del software Stockly:
-- índices de apoyo a las búsquedas, triggers que mantienen el stock
-- actualizado automáticamente al registrar entradas/salidas, y vistas
-- para las consultas que la aplicación necesita con mayor frecuencia.
-- =====================================================================

DROP DATABASE IF EXISTS stockly_db;
CREATE DATABASE stockly_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE stockly_db;

-- ---------------------------------------------------------------------
-- Tabla: roles
-- ---------------------------------------------------------------------
CREATE TABLE roles (
    id_rol          INT AUTO_INCREMENT,
    nombre_rol      VARCHAR(30)  NOT NULL,
    descripcion     VARCHAR(150),
    CONSTRAINT pk_roles PRIMARY KEY (id_rol),
    CONSTRAINT uq_roles_nombre UNIQUE (nombre_rol)
);

-- ---------------------------------------------------------------------
-- Tabla: usuarios
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario       INT AUTO_INCREMENT,
    nombre_completo  VARCHAR(100) NOT NULL,
    nombre_usuario   VARCHAR(50)  NOT NULL,
    contrasena       VARCHAR(255) NOT NULL,
    correo           VARCHAR(100) NOT NULL,
    id_rol           INT NOT NULL,
    estado           ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (id_rol)
        REFERENCES roles (id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- Tabla: categorias
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
    id_categoria    INT AUTO_INCREMENT,
    nombre_categoria VARCHAR(60) NOT NULL,
    descripcion     VARCHAR(200),
    CONSTRAINT pk_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre_categoria)
);

-- ---------------------------------------------------------------------
-- Tabla: proveedores
-- ---------------------------------------------------------------------
CREATE TABLE proveedores (
    id_proveedor     INT AUTO_INCREMENT,
    nombre_proveedor VARCHAR(100) NOT NULL,
    nit              VARCHAR(20)  NOT NULL,
    telefono         VARCHAR(20),
    correo           VARCHAR(100),
    direccion        VARCHAR(150),
    CONSTRAINT pk_proveedores PRIMARY KEY (id_proveedor),
    CONSTRAINT uq_proveedores_nit UNIQUE (nit)
);

-- ---------------------------------------------------------------------
-- Tabla: clientes
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
    id_cliente      INT AUTO_INCREMENT,
    nombre_cliente  VARCHAR(100) NOT NULL,
    documento       VARCHAR(20),
    telefono        VARCHAR(20),
    correo          VARCHAR(100),
    CONSTRAINT pk_clientes PRIMARY KEY (id_cliente),
    CONSTRAINT uq_clientes_documento UNIQUE (documento)
);

-- ---------------------------------------------------------------------
-- Tabla: tallas
-- ---------------------------------------------------------------------
CREATE TABLE tallas (
    id_talla     INT AUTO_INCREMENT,
    nombre_talla VARCHAR(10) NOT NULL,
    CONSTRAINT pk_tallas PRIMARY KEY (id_talla),
    CONSTRAINT uq_tallas_nombre UNIQUE (nombre_talla)
);

-- ---------------------------------------------------------------------
-- Tabla: colores
-- ---------------------------------------------------------------------
CREATE TABLE colores (
    id_color     INT AUTO_INCREMENT,
    nombre_color VARCHAR(30) NOT NULL,
    codigo_hex   CHAR(7),
    CONSTRAINT pk_colores PRIMARY KEY (id_color),
    CONSTRAINT uq_colores_nombre UNIQUE (nombre_color)
);

-- ---------------------------------------------------------------------
-- Tabla: productos
-- ---------------------------------------------------------------------
CREATE TABLE productos (
    id_producto      INT AUTO_INCREMENT,
    nombre_producto  VARCHAR(100) NOT NULL,
    descripcion      VARCHAR(255),
    marca            VARCHAR(50),
    genero           ENUM('HOMBRE','MUJER','UNISEX','NINO','NINA') NOT NULL DEFAULT 'UNISEX',
    precio_compra    DECIMAL(10,2) NOT NULL,
    precio_venta     DECIMAL(10,2) NOT NULL,
    id_categoria     INT NOT NULL,
    id_proveedor     INT NOT NULL,
    estado           ENUM('ACTIVO','DESCONTINUADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_productos PRIMARY KEY (id_producto),
    CONSTRAINT fk_productos_categoria FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_productos_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedores (id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_productos_precios CHECK (precio_venta >= precio_compra),
    CONSTRAINT ck_productos_precio_compra CHECK (precio_compra >= 0),
    CONSTRAINT ck_productos_precio_venta CHECK (precio_venta >= 0)
);

-- ---------------------------------------------------------------------
-- Tabla: variantes_producto (producto + talla + color = referencia única con QR)
-- ---------------------------------------------------------------------
CREATE TABLE variantes_producto (
    id_variante     INT AUTO_INCREMENT,
    id_producto     INT NOT NULL,
    id_talla        INT NOT NULL,
    id_color        INT NOT NULL,
    sku             VARCHAR(30) NOT NULL,
    codigo_qr       VARCHAR(100) NOT NULL,
    stock_actual    INT NOT NULL DEFAULT 0,
    stock_minimo    INT NOT NULL DEFAULT 5,
    CONSTRAINT pk_variantes PRIMARY KEY (id_variante),
    CONSTRAINT uq_variantes_sku UNIQUE (sku),
    CONSTRAINT uq_variantes_qr UNIQUE (codigo_qr),
    CONSTRAINT uq_variantes_combinacion UNIQUE (id_producto, id_talla, id_color),
    CONSTRAINT fk_variantes_producto FOREIGN KEY (id_producto)
        REFERENCES productos (id_producto)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_variantes_talla FOREIGN KEY (id_talla)
        REFERENCES tallas (id_talla)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_variantes_color FOREIGN KEY (id_color)
        REFERENCES colores (id_color)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_variantes_stock CHECK (stock_actual >= 0),
    CONSTRAINT ck_variantes_stock_minimo CHECK (stock_minimo >= 0)
);

-- ---------------------------------------------------------------------
-- Tabla: movimientos_inventario (registro general de entradas y salidas)
-- ---------------------------------------------------------------------
CREATE TABLE movimientos_inventario (
    id_movimiento    INT AUTO_INCREMENT,
    id_variante      INT NOT NULL,
    id_usuario       INT NOT NULL,
    tipo_movimiento  ENUM('ENTRADA','SALIDA') NOT NULL,
    cantidad         INT NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones    VARCHAR(200),
    CONSTRAINT pk_movimientos PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimientos_variante FOREIGN KEY (id_variante)
        REFERENCES variantes_producto (id_variante)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_movimientos_cantidad CHECK (cantidad > 0)
);

-- ---------------------------------------------------------------------
-- Tabla: entradas (especialización 1:1 de movimientos tipo ENTRADA)
-- ---------------------------------------------------------------------
CREATE TABLE entradas (
    id_entrada          INT AUTO_INCREMENT,
    id_movimiento       INT NOT NULL,
    id_proveedor        INT NOT NULL,
    numero_factura      VARCHAR(30),
    precio_unitario_compra DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_entradas PRIMARY KEY (id_entrada),
    CONSTRAINT uq_entradas_movimiento UNIQUE (id_movimiento),
    CONSTRAINT fk_entradas_movimiento FOREIGN KEY (id_movimiento)
        REFERENCES movimientos_inventario (id_movimiento)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_entradas_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedores (id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_entradas_precio CHECK (precio_unitario_compra >= 0)
);

-- ---------------------------------------------------------------------
-- Tabla: salidas (especialización 1:1 de movimientos tipo SALIDA)
-- ---------------------------------------------------------------------
CREATE TABLE salidas (
    id_salida            INT AUTO_INCREMENT,
    id_movimiento        INT NOT NULL,
    id_cliente           INT NULL,
    metodo_pago          ENUM('EFECTIVO','TARJETA','TRANSFERENCIA') NOT NULL DEFAULT 'EFECTIVO',
    precio_unitario_venta DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_salidas PRIMARY KEY (id_salida),
    CONSTRAINT uq_salidas_movimiento UNIQUE (id_movimiento),
    CONSTRAINT fk_salidas_movimiento FOREIGN KEY (id_movimiento)
        REFERENCES movimientos_inventario (id_movimiento)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_salidas_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes (id_cliente)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT ck_salidas_precio CHECK (precio_unitario_venta >= 0)
);

-- =====================================================================
-- ÍNDICES ADICIONALES DE APOYO A CONSULTAS FRECUENTES DEL SOFTWARE
-- (las columnas UNIQUE ya quedan indexadas automáticamente por MySQL)
-- =====================================================================
CREATE INDEX ix_productos_nombre ON productos (nombre_producto);
CREATE INDEX ix_movimientos_fecha ON movimientos_inventario (fecha_movimiento);
CREATE INDEX ix_movimientos_tipo ON movimientos_inventario (tipo_movimiento);
CREATE INDEX ix_variantes_producto ON variantes_producto (id_producto);

-- =====================================================================
-- TRIGGERS: actualización automática del stock de la variante al
-- registrar una entrada o una salida (regla de negocio del software:
-- el stock siempre se mueve a través de movimientos_inventario y su
-- especialización entradas/salidas, nunca se edita directamente)
-- =====================================================================
DELIMITER $$

CREATE TRIGGER trg_entradas_after_insert
AFTER INSERT ON entradas
FOR EACH ROW
BEGIN
    UPDATE variantes_producto vp
    INNER JOIN movimientos_inventario mi ON mi.id_movimiento = NEW.id_movimiento
    SET vp.stock_actual = vp.stock_actual + mi.cantidad
    WHERE vp.id_variante = mi.id_variante;
END$$

CREATE TRIGGER trg_salidas_after_insert
AFTER INSERT ON salidas
FOR EACH ROW
BEGIN
    UPDATE variantes_producto vp
    INNER JOIN movimientos_inventario mi ON mi.id_movimiento = NEW.id_movimiento
    SET vp.stock_actual = vp.stock_actual - mi.cantidad
    WHERE vp.id_variante = mi.id_variante;
END$$

DELIMITER ;

-- =====================================================================
-- VISTAS: consultas que la aplicación Stockly requiere con más
-- frecuencia (consulta de existencias por QR, alertas de stock bajo
-- y kardex/histórico de movimientos)
-- =====================================================================
CREATE VIEW v_existencias AS
SELECT
    vp.id_variante,
    p.nombre_producto,
    p.marca,
    t.nombre_talla,
    c.nombre_color,
    vp.sku,
    vp.codigo_qr,
    vp.stock_actual,
    vp.stock_minimo,
    p.precio_venta
FROM variantes_producto vp
INNER JOIN productos p ON p.id_producto = vp.id_producto
INNER JOIN tallas t ON t.id_talla = vp.id_talla
INNER JOIN colores c ON c.id_color = vp.id_color;

CREATE VIEW v_stock_bajo AS
SELECT *
FROM v_existencias
WHERE stock_actual <= stock_minimo;

CREATE VIEW v_kardex AS
SELECT
    mi.id_movimiento,
    mi.tipo_movimiento,
    mi.fecha_movimiento,
    mi.cantidad,
    vp.sku,
    p.nombre_producto,
    u.nombre_completo AS usuario_responsable,
    e.numero_factura,
    e.precio_unitario_compra,
    s.metodo_pago,
    s.precio_unitario_venta
FROM movimientos_inventario mi
INNER JOIN variantes_producto vp ON vp.id_variante = mi.id_variante
INNER JOIN productos p ON p.id_producto = vp.id_producto
INNER JOIN usuarios u ON u.id_usuario = mi.id_usuario
LEFT JOIN entradas e ON e.id_movimiento = mi.id_movimiento
LEFT JOIN salidas s ON s.id_movimiento = mi.id_movimiento;
