package main.java.v1;

import log4Mats.LogLevel;
import main.java.dao.Dao;
import main.java.models.Cliente;
import main.java.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

public class DaoCliente_v1 implements Dao<Cliente> {

    @Override
    public Cliente get(int id) {
        return null;
    }


    public void insert(Cliente c) {

        try (Connection conn = ConnectionFactory.getConnectionDriverM()) {

            String sql = "INSERT INTO usuarios (nombre, dni, telefono) VALUES (?, ?, ?)";

            // Le decimos a JDBC que capture las columnas con AUTO_INCREMENT
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getDni());
            ps.setInt(3, c.getTelefono());

            ps.executeUpdate();

            // Result set con las columnas auto-incrementadas
            ResultSet keys = ps.getGeneratedKeys();

            // La primera (y normalmente la unica) clave generada
            if (keys.next()) {
                c.setIdCliente(keys.getInt(1));
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when inserting Cliente");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    @Override
    public void insert(List<Cliente> entity) {

    }

    @Override
    public void update(Cliente c) {

    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public boolean exists(Cliente c) {
        return false;
    }

    public List<Cliente> findAll() {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDriverM()) {
            String sql = "SELECT * FROM usuarios";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Cliente c = new Cliente(rs.getString("nombre"),
                        rs.getInt("dni"),
                        rs.getInt("telefono"));
                c.setDni(rs.getInt("id"));

                clientes.add(c);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Clientes");
            System.err.println(sqle.getLocalizedMessage());
        }

        return clientes;
    }

    @Override
    public List<Cliente> findByAttribute(Cliente filtro) {
        return List.of();
    }


}
