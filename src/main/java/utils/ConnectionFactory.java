package main.java.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que devuelve una conexion con DriverManager o DataSource a las bases de datos prac2 o prac2migra.
 * @author mati
 */
public class ConnectionFactory {

    // Original, puerto 3306 o 3307:
    private static final String URL = "jdbc:mysql://localhost:3306/prac2?useSSL=false&serverTimezone=UTC" +
            "&allowPublicKeyRetrieval=true";
//    private static final String URL = "jdbc:mysql://localhost:3307/prac2?useSSL=false&serverTimezone=UTC" +
//            "&allowPublicKeyRetrieval=true";

    // Migrada, puerto 3306 o 3307:
    private static final String URL_MIGRA = "jdbc:mysql://localhost:3306/prac2migra?useSSL=false&serverTimezone=UTC" +
            "&allowPublicKeyRetrieval=true";
//    private static final String URL_MIGRA = "jdbc:mysql://localhost:3307/prac2migra?useSSL=false&serverTimezone=UTC" +
//            "&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASS = "root";

    // Data source
    private static HikariDataSource ds;
    private static HikariDataSource dsMigra;

    // Inicializar para toda la clase:
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setMaximumPoolSize(10);

        ds = new HikariDataSource(config);

        config.setJdbcUrl(URL_MIGRA);

        dsMigra = new HikariDataSource(config);
    }

    /**
     * Metodo que devuelve una conexion con DriverManager a la base de datos prac2
     * Prestar atencion a url segun puerto.
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnectionDmOriginal() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Metodo que devuelve una conexion con DriverManager a la base de datos prac2migra
     * Prestar atencion a url segun puerto.
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnectionDmMigrada() throws SQLException {
        return DriverManager.getConnection(URL_MIGRA, USER, PASS);
    }

    /**
     * Metodo que devuelve una conexion con DataSource a la base de datos prac2
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnectionDS() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Metodo que devuelve una conexion con DataSource a la base de datos prac2migra
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnectionDsMigra() throws SQLException {
        return dsMigra.getConnection();
    }


}
