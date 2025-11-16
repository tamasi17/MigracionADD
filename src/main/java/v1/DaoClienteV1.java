package main.java.v1;

import log4Mats.LogLevel;
import main.java.interfaces.DaoClientes;
import main.java.models.Cliente;
import main.java.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

public class DaoClienteV1 implements DaoClientes<Cliente> {

    @Override
    public Cliente get(int id) {

        String sql = "SELECT * FROM clientes WHERE idCliente = ?";

        Cliente cliente = null;

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                cliente = new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getInt("dni"),
                        rs.getInt("telefono"));
                cliente.setIdCliente(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de cliente " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return cliente;
    }


    public Cliente getMigra(int id) {

        String sql = "SELECT * FROM clientesMigra WHERE idCliente_migra = ?";

        Cliente cliente = null;

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                cliente = new Cliente(
                        rs.getString("nombre_migra"),
                        rs.getString("apellido1_migra"),
                        rs.getInt("dni_migra"),
                        rs.getInt("telefono_migra"),
                        rs.getBoolean("activo_migra"));
                cliente.setIdCliente(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de cliente con idCliente_migra " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return cliente;

    }

    public void insertOne(Cliente c) {

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, dni, telefono) VALUES (?, ?, ?, ?, ?)";

            // Le decimos a JDBC que capture las columnas con AUTO_INCREMENT
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido1());
            ps.setString(3, c.getApellido2());
            ps.setInt(4, c.getDni());
            ps.setInt(5, c.getTelefono());

            ps.executeUpdate();

            // Result set con las columnas auto-incrementadas
            ResultSet key = ps.getGeneratedKeys();

            // La primera (y normalmente la unica) clave generada --> id de cliente
            if (key.next()) {
                c.setIdCliente(key.getInt(1));
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
            int i = 0;
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


    public void insertManyMigra(List<Cliente> entity) {

        String sql = "INSERT INTO clientesMigra (nombre_migra, apellido1_migra, dni_migra, telefono_migra, activo_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Cliente cliente : entity) {
                ps.setString(1, cliente.getNombre());
                ps.setString(2, cliente.getApellido1());
                ps.setInt(3, cliente.getDni());
                ps.setInt(4, cliente.getTelefono());
                ps.setBoolean(5, cliente.isActivo());
                ps.setBoolean(6, cliente.isMigrado());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idCliente)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
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
    public void updateOne(Cliente c) {

        // Recuerda cerrar comilla simple
        String sql = "UPDATE clientes SET nombre = '" + c.getNombre() + "', " +
                " apellido1 = '" + c.getApellido1() + "', " +
                " apellido2 = '" + c.getApellido2() + "', " +
                " telefono = " + c.getTelefono() + ", " +
                " activo = " + c.isActivo() +
                " WHERE idCliente = " + c.getIdCliente() + ";";

        // Comprobamos query
//        System.out.println(sql);

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            int rows = ps.executeUpdate();

            // ps.executeUpdate devuelve el numero de filas afectadas
            if (rows > 0) {
                getLogger().trace("[" + c.getIdCliente() + "] Cliente actualizado.");
            } else {
                getLogger().warn("Cliente no actualizado, check ID: " + c.getIdCliente());
            }


        } catch (SQLException e) {
            getLogger().error("Could not update info of Client: " + c.getIdCliente());
        }
    }

    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM clientes WHERE idCliente = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0){
                getLogger().trace("Eliminado cliente "+ id);
            } else {
                getLogger().warn("No se eliminó cliente "+ id);
            }

            } catch (SQLException sqle) {
            getLogger().error("Error al eliminar cliente "+ id +
                    "\n"+ sqle.getLocalizedMessage());
        }
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM clientes WHERE idCliente = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            // Si hay info, rs.next devuelve true, si no, no.
            if (rs.next()) { return true;}

        } catch (SQLException sqle) {
            getLogger().error("Error confirmando si existe cliente " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        // Si ha llegado aqui es porque no hay info, exists=false.
         return false;
    }

    public List<Cliente> findAll() {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM clientes";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getInt("dni"),
                        rs.getInt("telefono"));
                c.setIdCliente(rs.getInt(1));

                clientes.add(c);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Clientes");
            System.err.println(sqle.getLocalizedMessage());
        }

        return clientes;
    }


    public List<Cliente> findAllMigra() {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM clientes";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getInt("dni"),
                        rs.getInt("telefono"),
                        rs.getBoolean("activo"));
                c.setIdCliente(rs.getInt(1));

                clientes.add(c);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Clientes");
            System.err.println(sqle.getLocalizedMessage());
        }

        return clientes;
    }



    @Override
    public List<Cliente> findByAttributes(String nombre, String apellido1) {
        String sql = "SELECT * FROM clientes WHERE nombre = ? AND apellido1 = ?";

        List<Cliente> encontrados = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellido1);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Cliente c = new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getInt("dni"),
                        rs.getInt("telefono"));
                c.setIdCliente(rs.getInt(1));

                encontrados.add(c);
            }
        } catch (SQLException sqle) {
            getLogger().error("No se ha podido encontrar según: "+ nombre +" y "+ apellido1);
            System.err.println(sqle.getLocalizedMessage());
        }

        return encontrados;
    }


    public void loadMigra(List<Cliente> entity, Connection connection) {

        String sql = "INSERT INTO clientesMigra " +
                "(nombre_migra, apellido1_migra, dni_migra, telefono_migra, activo_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {

            // Transaccion atomica se ejecuta en JdbcMigrator.migrarDb()
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Cliente cliente : entity) {
                ps.setString(1, cliente.getNombre());
                ps.setString(2, cliente.getApellido1());
                ps.setInt(3, cliente.getDni());
                ps.setInt(4, cliente.getTelefono());
                ps.setBoolean(5, cliente.isActivo());
                ps.setBoolean(6, cliente.isMigrado());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idCliente)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdCliente(keys.getInt(1));
                i++;
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR,
                    "Connection(DM) not established when loading: "+ sqle.getLocalizedMessage());
        }
    }

}
