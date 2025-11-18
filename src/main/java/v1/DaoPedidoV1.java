package main.java.v1;

import log4Mats.LogLevel;
import main.java.interfaces.DaoPedidos;
import main.java.interfaces.DaoProductos;
import main.java.models.Pedido;
import main.java.models.Producto;
import main.java.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

public class DaoPedidoV1 implements DaoPedidos<Pedido> {

    @Override
    public Pedido get(int id) {

        String sql = "SELECT * FROM pedidos WHERE idPedido = ?";

        Pedido pedido = null;

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                pedido = new Pedido(
                        rs.getDate("fecha"),
                        rs.getDouble("precio"),
                        rs.getInt("clienteId"),
                        rs.getInt("productoId"));
                pedido.setIdPedido(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de pedido " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return pedido;
    }


    public Pedido getMigra(int id) {

        String sql = "SELECT * FROM productosMigra WHERE idProductos_migra = ?";

        Pedido pedido = null;

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                pedido = new Pedido(
                        rs.getDate("fecha_migra"),
                        rs.getDouble("precio_migra"),
                        rs.getInt("clienteId_migra"),
                        rs.getInt("productoId_migra"));
                pedido.setMigrado(true);
                pedido.setIdPedido(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de cliente con idProducto_migra " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return pedido;

    }

    public int insertOne(Pedido pedido) {

        int id=0;
        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            String sql = "INSERT INTO pedidos (fecha, precio, clienteId, productoId ) VALUES (?, ?, ?, ?)";

            // Le decimos a JDBC que capture las columnas con AUTO_INCREMENT
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setDate(1, pedido.getFechaPedido());
            ps.setDouble(2, pedido.getPrecio());
            ps.setInt(3, pedido.getClienteId());
            ps.setInt(4, pedido.getProductoId());

            ps.executeUpdate();

            // Result set con las columnas auto-incrementadas
            ResultSet key = ps.getGeneratedKeys();

            // La primera (y normalmente la unica) clave generada --> id de pedido
            if (key.next()) {
                id = key.getInt(1);
                pedido.setIdPedido(id);

            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when inserting Pedido");
            System.err.println(sqle.getLocalizedMessage());
        }

        return id;
    }

    @Override
    public void insertMany(List<Pedido> entity) {

        String sql = "INSERT INTO pedidos (fecha, precio, clienteId, productoId ) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Pedido pedido : entity) {
                ps.setDate(1, pedido.getFechaPedido());
                ps.setDouble(2, pedido.getPrecio());
                ps.setInt(3, pedido.getClienteId());
                ps.setInt(4, pedido.getProductoId());
                ps.addBatch();
            }

            ps.executeBatch();


            // Recuperamos clave generada (idProducto)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdPedido(keys.getInt(1));
                i++;
            }

            connection.setAutoCommit(true);

            // tira BatchException?

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DM) not established when inserting List<Pedido>");
            System.err.println(sqle.getLocalizedMessage());
        }
    }


    public void insertManyMigra(List<Pedido> entity) {

        String sql = "INSERT INTO productosMigra (fecha_migra, precio_migra, clienteId_migra, productoId_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (Pedido pedido : entity) {
                ps.setDate(1, pedido.getFechaPedido());
                ps.setDouble(2, pedido.getPrecio());
                ps.setInt(3, pedido.getClienteId());
                ps.setInt(4, pedido.getProductoId());
                ps.setBoolean(5, pedido.isMigrado());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idCliente)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdPedido(keys.getInt(1));
                i++;
            }

            connection.setAutoCommit(true);

            // tira BatchException?

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR,
                    "Connection(DM) not established when inserting List<Pedido> into pedidosMigra");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    public void updateOne(Pedido p) {


        String sql = "UPDATE pedidos SET fecha = ?, precio = ?, clienteId = ?, productoId = ? " +
                "WHERE idPedido = ?";

        // Comprobamos query
        System.out.println(sql);

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setDate(1, java.sql.Date.valueOf(p.getFechaPedido().toLocalDate()));
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getClienteId());
            ps.setInt(4, p.getProductoId());
            ps.setInt(5, p.getIdPedido());

            int rows = ps.executeUpdate();

            // ps.executeUpdate devuelve el numero de filas afectadas
            if (rows > 0) {
                getLogger().trace("[" + p.getIdPedido() + "] Pedido actualizado.");
            } else {
                getLogger().warn("Pedido no actualizado, check ID: " + p.getIdPedido());
            }


        } catch (SQLException sqle) {
            getLogger().error("Could not update info of Pedido: "
                    + p.getIdPedido() + "\n"+ sqle.getLocalizedMessage());
        }
    }

    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM pedidos WHERE idPedido = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0){
                getLogger().trace("Eliminado pedido "+ id);
            } else {
                getLogger().warn("No se eliminó pedido "+ id);
            }

            } catch (SQLException sqle) {
            getLogger().error("Error al eliminar pedido "+ id +
                    "\n"+ sqle.getLocalizedMessage());
        }
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM pedidos WHERE idPedido = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            // Si hay info, rs.next devuelve true, si no, no.
            if (rs.next()) { return true;}

        } catch (SQLException sqle) {
            getLogger().error("Error confirmando si existe pedido " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        // Si ha llegado aqui es porque no hay info, exists=false.
         return false;
    }

    public List<Pedido> findAll() {

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM pedidos";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getDate("fecha"),
                        rs.getDouble("precio"),
                        rs.getInt("clienteId"),
                        rs.getInt("productoId"));
                p.setIdPedido(rs.getInt(1));

                pedidos.add(p);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Pedidos");
            System.err.println(sqle.getLocalizedMessage());
        }

        return pedidos;
    }


    public List<Pedido> findAllMigra(Connection conn) {

        List<Pedido> pedidos = new ArrayList<>();

        try {
            String sql = "SELECT * FROM pedidosMigra";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getDate("fecha_migra"),
                        rs.getDouble("precio_migra"),
                        rs.getInt("clienteId_migra"),
                        rs.getInt("productoId_migra"));
                p.setMigrado(true);
                p.setIdPedido(rs.getInt(1)); // recuerda asignar id al crear!

                pedidos.add(p);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing PedidosMigra");
            System.err.println(sqle.getLocalizedMessage());
        }

        return pedidos;
    }




    public List<Pedido> findByAttributes(int clienteId, int productoId) {
        String sql = "SELECT * FROM pedidos WHERE clienteId = ? AND productoId = ?";

        List<Pedido> encontrados = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clienteId);
            ps.setDouble(2, productoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Pedido p = new Pedido(
                        rs.getDate("fecha"),
                        rs.getDouble("precio"),
                        rs.getInt("clienteId"),
                        rs.getInt("productoId"));
                p.setIdPedido(rs.getInt(1));

                encontrados.add(p);
            }
        } catch (SQLException sqle) {
            getLogger().error("No se ha podido encontrar según: "+ clienteId +" y "+ productoId);
            System.err.println(sqle.getLocalizedMessage());
        }

        return encontrados;
    }


    public void loadMigra(List<Pedido> entity, Connection connection) {

        String sql = "INSERT INTO pedidosMigra " +
                "(fecha_migra, precio_migra, clienteId_migra, productoId_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {

            // Transaccion atomica se ejecuta en JdbcMigrator.migrarDb()
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Pedido pedido : entity) {
                ps.setDate(1, pedido.getFechaPedido());
                ps.setDouble(2, pedido.getPrecio());
                ps.setInt(3, pedido.getClienteId());
                ps.setInt(4, pedido.getProductoId());
                ps.setBoolean(5, true);
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idProducto)S
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada pedido (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdPedido(keys.getInt(1));
                i++;
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR,
                    "Connection(DM) not established when loading: "+ sqle.getLocalizedMessage());
        }
    }

}
