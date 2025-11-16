CREATE DATABASE prac2;
CREATE DATABASE prac2migra;
SHOW TABLES FROM prac2;

-- ORIGINAL --
USE prac2;
SELECT * FROM clientes;
SELECT * FROM clientes WHERE idCliente = 8;
SELECT nombre, apellido1 FROM clientes WHERE idCliente =8;
DELETE FROM clientes WHERE idCliente = 2;
DROP TABLE clientes;

-- MIGRADA --
USE prac2migra;
SELECT * FROM clientesMigra;	
SELECT * FROM clientesMigra WHERE idCliente = 8;
SELECT nombre, apellido1 FROM clientesMigra WHERE idCliente =8;
DROP TABLE clientesMigra;
