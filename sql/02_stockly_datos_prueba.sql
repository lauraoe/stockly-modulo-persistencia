-- =====================================================================
-- PROYECTO: STOCKLY - Sistema de Gestión de Inventarios para Tienda de Ropa
-- EVIDENCIA: GA6-220501096-AA2-EV03 - Script bases de datos del proyecto
-- SCRIPT: Datos de prueba
-- MOTOR: MySQL 8.0
-- -----------------------------------------------------------------------
-- Ejecutar DESPUÉS de 01_stockly_modelo_fisico.sql sobre la base de
-- datos stockly_db. Este script inserta un conjunto mínimo de datos
-- que permite validar visualmente en MySQL Workbench que las llaves,
-- restricciones, triggers de stock y vistas funcionan correctamente.
-- =====================================================================

USE stockly_db;

-- Roles y usuarios
INSERT INTO roles (nombre_rol, descripcion) VALUES
    ('ADMINISTRADOR', 'Acceso total al sistema'),
    ('VENDEDOR', 'Registra ventas y consulta existencias'),
    ('BODEGUERO', 'Registra entradas de mercancía');

INSERT INTO usuarios (nombre_completo, nombre_usuario, contrasena, correo, id_rol) VALUES
    ('Laura Ordoñez', 'laura.ordonez', 'hash_pendiente_1', 'laura@stockly.com', 1),
    ('Pedro Gamboa', 'pedro.gamboa', 'hash_pendiente_2', 'pedro@stockly.com', 2);

-- Catálogos del negocio
INSERT INTO categorias (nombre_categoria, descripcion) VALUES
    ('Camisetas', 'Camisetas y tops en general'),
    ('Pantalones', 'Pantalones, jeans y leggins');

INSERT INTO proveedores (nombre_proveedor, nit, telefono, correo, direccion) VALUES
    ('Textiles del Valle S.A.S.', '900123456-1', '3001234567', 'ventas@textilesvalle.com', 'Cali, Colombia');

INSERT INTO clientes (nombre_cliente, documento, telefono, correo) VALUES
    ('Mariana Rojas', '1094567890', '3109876543', 'mariana.rojas@example.com');

INSERT INTO tallas (nombre_talla) VALUES ('S'), ('M'), ('L');

INSERT INTO colores (nombre_color, codigo_hex) VALUES
    ('Negro', '#000000'),
    ('Blanco', '#FFFFFF');

-- Productos y variantes
INSERT INTO productos (nombre_producto, descripcion, marca, genero, precio_compra, precio_venta, id_categoria, id_proveedor) VALUES
    ('Camiseta básica cuello redondo', '100% algodón', 'Stockly Basics', 'UNISEX', 15000.00, 35000.00, 1, 1);

INSERT INTO variantes_producto (id_producto, id_talla, id_color, sku, codigo_qr, stock_actual, stock_minimo) VALUES
    (1, 2, 1, 'CAM-BAS-M-NEG', 'QR-CAM-BAS-M-NEG-001', 0, 5);

-- Movimiento de ENTRADA (compra al proveedor) -> el trigger debe subir stock_actual a 20
INSERT INTO movimientos_inventario (id_variante, id_usuario, tipo_movimiento, cantidad, observaciones) VALUES
    (1, 2, 'ENTRADA', 20, 'Compra inicial de inventario');

INSERT INTO entradas (id_movimiento, id_proveedor, numero_factura, precio_unitario_compra) VALUES
    (LAST_INSERT_ID(), 1, 'FAC-0001', 15000.00);

-- Movimiento de SALIDA (venta a cliente) -> el trigger debe bajar stock_actual a 17
INSERT INTO movimientos_inventario (id_variante, id_usuario, tipo_movimiento, cantidad, observaciones) VALUES
    (1, 1, 'SALIDA', 3, 'Venta en tienda física');

INSERT INTO salidas (id_movimiento, id_cliente, metodo_pago, precio_unitario_venta) VALUES
    (LAST_INSERT_ID(), 1, 'TARJETA', 35000.00);

-- Verificación rápida (opcional, ejecutar de forma manual en Workbench):
-- SELECT * FROM v_existencias;
-- SELECT * FROM v_stock_bajo;
-- SELECT * FROM v_kardex;
