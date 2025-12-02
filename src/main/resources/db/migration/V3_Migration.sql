CREATE DATABASE prac2;
CREATE DATABASE prac2migra;

CREATE TABLE IF NOT EXISTS clientes (
                    idCliente INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    apellido1 VARCHAR(100) NOT NULL,
                    apellido2 VARCHAR(100) NOT NULL,
                    dni INT NOT NULL UNIQUE,
                    telefono BIGINT,
                    activo BOOLEAN DEFAULT TRUE,
                    fecha_registro DATE
                );
                
CREATE TABLE IF NOT EXISTS clientesMigra (
                    idCliente_migra INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_migra VARCHAR(100) NOT NULL,
                    apellido1_migra VARCHAR(100) NOT NULL,
                    dni_migra INT NOT NULL UNIQUE,
                    telefono_migra BIGINT,
                    activo_migra BOOLEAN DEFAULT TRUE,
                    migrado BOOLEAN DEFAULT FALSE
                );
                
CREATE TABLE IF NOT EXISTS productos (
                    idProducto INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(255),
                    precio DECIMAL(10,2) NOT NULL,
                    disponible BOOLEAN NOT NULL DEFAULT TRUE
                );
                
CREATE TABLE IF NOT EXISTS productosMigra (
                    idProducto_migra INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_migra VARCHAR(100) NOT NULL,
                    descripcion_migra VARCHAR(255),
                    precio_migra DECIMAL(10,2) NOT NULL,
                    disponible_migra BOOLEAN NOT NULL DEFAULT TRUE,
                    migrado BOOLEAN NOT NULL DEFAULT TRUE
                );


CREATE TABLE IF NOT EXISTS pedidos (
                    idPedido INT AUTO_INCREMENT PRIMARY KEY,
                    fecha DATE NOT NULL,
                    precio DECIMAL(10,2) NOT NULL,
                    clienteId INT NOT NULL,
                    CONSTRAINT fk_pedido_cliente
                        FOREIGN KEY (clienteId)
                        REFERENCES clientes(idCliente)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    productoId INT NOT NULL,
                    CONSTRAINT fk_pedido_producto
                        FOREIGN KEY (productoId)
                        REFERENCES productos(idProducto)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
                
CREATE TABLE IF NOT EXISTS pedidosMigra (
                    idPedido_migra INT AUTO_INCREMENT PRIMARY KEY,
                    fecha_migra DATE NOT NULL,
                    precio_migra DECIMAL(10,2) NOT NULL,
                    migrado BOOLEAN NOT NULL DEFAULT TRUE,
                    clienteId_migra INT NOT NULL,
                    CONSTRAINT fk_pedido_cliente
                        FOREIGN KEY (clienteId_migra)
                        REFERENCES clientesMigra(idCliente_migra)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    productoId_migra INT NOT NULL,
                    CONSTRAINT fk_pedido_producto
                        FOREIGN KEY (productoId_migra)
                        REFERENCES productosMigra(idProducto_migra)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
                
