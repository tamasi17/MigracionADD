package main.java.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import static main.java.logging.LoggerProvider.getLogger;

public class DatabaseSetup {


    public static void borrarTabla(String tabla){

        Set<String> tablasPermitidas = Set.of("clientes","pedidos", "productos");

        // si la tabla que pasamos por parametro no se encuentra
        // entre las tablas permitidas para borrado, tira excepcion
        if (!tablasPermitidas.contains(tabla)){
            throw new IllegalArgumentException(tabla + "no es un argumento valido.");
        }

        String sql = "DROP TABLE "+ tabla+ ";";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal();
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


    // SIN TERMINAR
    public static void crearTablaPedidos() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS pedidos (
                    idCliente INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    telefono BIGINT,
                    activo BOOLEAN NOT NULL,
                    fecha_registro DATE
                )
                """;
        try(Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla pedidos creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla clientes");
        }

    }




}
