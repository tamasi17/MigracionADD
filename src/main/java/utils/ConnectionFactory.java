package main.java.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que devuelve una conexion con DriverManager o DataSource.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:mysql://localhost:3307/usuarios?useSSL=false&serverTimezone=UTC" +
            "&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "root";

    // Data source
    private static HikariDataSource ds;

    // Inicializar para toda la clase:
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setMaximumPoolSize(10);

        ds = new HikariDataSource(config);
    }

    // Conexion con DriverManager
    public static Connection getConnectionDriverM() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Conexion con DataSource
    public static Connection getConnectionDS() throws SQLException {
        return ds.getConnection();
    }


}
