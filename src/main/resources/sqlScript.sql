CREATE DATABASE prac2;
CREATE DATABASE prac2migra;


-- ORIGINAL --
USE prac2;
SHOW TABLES FROM prac2;
SELECT * FROM clientes;
SELECT * FROM clientes WHERE idCliente = 8;
SELECT nombre, apellido1 FROM clientes WHERE idCliente =8;
DELETE FROM clientes WHERE idCliente = 2;
DROP TABLE clientes;

-- MIGRADA --
USE prac2migra;
SHOW TABLES FROM prac2migra;
SELECT * FROM clientesMigra;	
SELECT * FROM clientesMigra WHERE idCliente_migra = 4;
SELECT nombre, apellido1 FROM clientesMigra WHERE idCliente =8;
DROP TABLE clientesMigra;

-- PRODUCTOS --
USE prac2;
SHOW TABLES FROM prac2;
SELECT * FROM productos;
SELECT * FROM productos WHERE idProducto = 8;
SELECT nombre, descripcion, precio FROM productos WHERE idProducto =8;
DELETE FROM productos WHERE idProducto = 2;
DROP TABLE productos;

-- MIGRADA --
USE prac2migra;
SHOW TABLES FROM prac2migra;
SELECT * FROM productosMigra;	
SELECT * FROM productosMigra WHERE idProducto_migra = 4;
SELECT nombre, apellido1 FROM productosMigra WHERE idProducto_migra =8;
DROP TABLE productosMigra;

-- PEDIDOS --
USE prac2;
SHOW TABLES FROM prac2;
SELECT * FROM pedidos;
SELECT * FROM pedidos WHERE idPedido = 8;
SELECT idPedido, clienteId, productoId FROM pedidos WHERE idPedido =8;
DELETE FROM pedidos WHERE idPedido = 2;
DROP TABLE productos;

-- MIGRADA --
USE prac2migra;
SHOW TABLES FROM prac2migra;
SELECT * FROM productosMigra;	
SELECT * FROM productosMigra WHERE idProducto_migra = 4;
SELECT nombre, apellido1 FROM productosMigra WHERE idProducto_migra =8;
DROP TABLE productosMigra;

-- RECUENTO --
SELECT 'clientesMigra' AS tabla, COUNT(*) AS total FROM clientesMigra
UNION ALL
SELECT 'productosMigra', COUNT(*) FROM productosMigra
UNION ALL
SELECT 'pedidosMigra', COUNT(*) FROM pedidosMigra;