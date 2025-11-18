package main.java.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import static main.java.logging.LoggerProvider.getLogger;

public class DbSetup {


    public static void borrarTabla(String tabla){

        Set<String> tablasPermitidas = Set.of("clientes","pedidos", "productos");

        // si la tabla que pasamos por parametro no se encuentra
        // entre las tablas permitidas para borrado, tira excepcion
        if (!tablasPermitidas.contains(tabla)){
            throw new IllegalArgumentException(tabla + "no es un argumento valido.");
        }

        String sql = "DROP TABLE "+ tabla + ";";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal();
             Statement statement = connection.createStatement();){

            statement.executeUpdate(sql);

            getLogger().info("Tabla "+ tabla +" eliminada correctamente.");

        } catch (SQLException sqle) {
            getLogger().error("No se pudo borrar la tabla "+ tabla);
            System.err.println(sqle.getLocalizedMessage());

        }

    }

    public static void borrarTablaMigrada(String tabla){

        Set<String> tablasPermitidas = Set.of("clientesMigra","pedidosMigra", "productosMigra");

        // si la tabla que pasamos por parametro no se encuentra
        // entre las tablas permitidas para borrado, tira excepcion
        if (!tablasPermitidas.contains(tabla)){
            throw new IllegalArgumentException(tabla + "no es un argumento valido.");
        }

        String sql = "DROP TABLE "+ tabla+ ";";

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada();
             Statement statement = connection.createStatement();){

            statement.executeUpdate(sql);

            getLogger().info("Tabla "+ tabla +" eliminada correctamente.");

        } catch (SQLException sqle) {
            getLogger().error("No se pudo borrar la tabla "+ tabla);
            System.err.println(sqle.getLocalizedMessage());

        }

    }

    public static void crearTablaClientes() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS clientes (
                    idCliente INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    apellido1 VARCHAR(100) NOT NULL,
                    apellido2 VARCHAR(100) NOT NULL,
                    dni INT NOT NULL UNIQUE,
                    telefono BIGINT,
                    activo BOOLEAN DEFAULT TRUE,
                    fecha_registro DATE
                )
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla clientes creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla clientes");
        }
    }

    public static void crearTablaClientesMigrada() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS clientesMigra (
                    idCliente_migra INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_migra VARCHAR(100) NOT NULL,
                    apellido1_migra VARCHAR(100) NOT NULL,
                    dni_migra INT NOT NULL UNIQUE,
                    telefono_migra BIGINT,
                    activo_migra BOOLEAN DEFAULT TRUE,
                    migrado BOOLEAN DEFAULT FALSE
                )
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmMigrada()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla clientesMigra creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla clientesMigra");
        }

    }


    public static void crearTablaProductos() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS productos (
                    idProducto INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(255),
                    precio DECIMAL(10,2) NOT NULL,
                    disponible BOOLEAN NOT NULL DEFAULT TRUE
                );
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla productos creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla productos");
        }
    }

    public static void crearTablaProductosMigrada() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS productosMigra (
                    idProducto_migra INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_migra VARCHAR(100) NOT NULL,
                    descripcion_migra VARCHAR(255),
                    precio_migra DECIMAL(10,2) NOT NULL,
                    disponible_migra BOOLEAN NOT NULL DEFAULT TRUE,
                    migrado BOOLEAN NOT NULL DEFAULT TRUE
                );
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmMigrada()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla productosMigra creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla productosMigra");
        }
    }


    public static void crearTablaPedidos() throws SQLException {

        String sql = """
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
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla pedidos creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla pedidos");
        }
    }


    public static void crearTablaPedidosMigrada() throws SQLException {

        String sql = """
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
                """;

        try(Connection connection = ConnectionFactory.getConnectionDmMigrada()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla pedidosMigra creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla pedidosMigra");
        }
    }


}
