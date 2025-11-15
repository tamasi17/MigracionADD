package main.java.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static main.java.logging.LoggerProvider.getLogger;

public class DatabaseSetup {


    void borrarTablas(){
        String sql = "DROP";
    }

    public static void crearTablaClientes() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    dni INT NOT NULL UNIQUE,
                    telefono BIGINT,
                    activo BOOLEAN NOT NULL,
                    fecha_registro DATE
                )
                """;
        try(Connection connection = ConnectionFactory.getConnectionDriverM()){

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
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    telefono BIGINT,
                    activo BOOLEAN NOT NULL,
                    fecha_registro DATE
                )
                """;
        try(Connection connection = ConnectionFactory.getConnectionDriverM()){

            Statement st = connection.createStatement();
            st.execute(sql);

            getLogger().info("Tabla pedidos creada correctamente");

        } catch (SQLException sqle) {
            throw new SQLException("No se pudo crear la tabla clientes");
        }

    }




}
