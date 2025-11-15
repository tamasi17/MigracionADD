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


    public void insertOne(Cliente c) {

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {

            String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, dni, telefono) VALUES (?, ?, ?, ?, ?)";

            // Le decimos a JDBC que capture las columnas con AUTO_INCREMENT
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido1());
            ps.setString(3, c.getApellido2());
            ps.setInt(4, c.getDni());
            ps.setInt(5, c.getTelefono());

            ps.executeUpdate();

            // Result set con las columnas auto-incrementadas
            ResultSet keys = ps.getGeneratedKeys();

            // La primera (y normalmente la unica) clave generada --> id de cliente
            if (keys.next()) {
                c.setIdCliente(keys.getInt(1));
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when inserting Cliente");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    public void insertMany(List<Cliente> entity) {

        String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, dni, telefono, activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Cliente cliente : entity) {
                ps.setString(1, cliente.getNombre());
                ps.setString(2, cliente.getApellido1());
                ps.setString(3, cliente.getApellido2());
                ps.setInt(4, cliente.getDni());
                ps.setInt(5, cliente.getTelefono());
                ps.setBoolean(6, true);
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idCliente)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 1;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdCliente(keys.getInt(1));
                i++;
            }

            connection.setAutoCommit(true);

            // tira BatchException?

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DM) not established when inserting Cliente list");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    @Override
    public void update(Cliente c) {


        String sql = "";

        /*
        UPDATE clientes
        SET nombre = 'Juan Pérez',
        telefono = 555123456,
        activo = 1
        WHERE id_cliente = 10;
         */

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


        } catch (SQLException e) {
            getLogger().error("Could not update info of Client: " + c.getIdCliente());
        }
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

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM usuarios";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

//            while (rs.next()) {
//                Cliente c = new Cliente(rs.getString("nombre"),
//                        rs.getInt("dni"),
//                        rs.getInt("telefono"));
//                c.setDni(rs.getInt("id")); // ESTO NO - HAY QUE CONSEGUIR LA GENERATED KEY
//
//                clientes.add(c);
//            }

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
