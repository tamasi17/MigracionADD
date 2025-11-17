package main.java.v1;

import log4Mats.LogLevel;
import main.java.interfaces.Dao;
import main.java.interfaces.DaoClientes;
import main.java.models.Cliente;
import main.java.models.Producto;
import main.java.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

public class DaoProductoV1 implements Dao<Producto> {

    @Override
    public Producto get(int id) {

        String sql = "SELECT * FROM productos WHERE idProducto = ?";

        Producto producto = null;

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                producto = new Producto(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"));
                producto.setIdProducto(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de cliente " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return producto;
    }


    public Producto getMigra(int id) {

        String sql = "SELECT * FROM productosMigra WHERE idProductos_migra = ?";

        Producto producto = null;

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Obligatorio mover el cursor!

                producto = new Producto(
                        rs.getString("nombre_migra"),
                        rs.getString("descripcion_migra"),
                        rs.getInt("precio_migra"),
                        rs.getBoolean("disponible_migra"));
                producto.setMigrado(true);
                producto.setIdProducto(rs.getInt(1)); // recuerda asignar id al crear!
            }

        } catch (SQLException sqle) {
            getLogger().error("Error recogiendo informacion de cliente con idProducto_migra " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        return producto;

    }

    public void insertOne(Producto p) {

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            String sql = "INSERT INTO productos (nombre, descripcion, precio) VALUES (?, ?, ?)";

            // Le decimos a JDBC que capture las columnas con AUTO_INCREMENT
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());

            ps.executeUpdate();

            // Result set con las columnas auto-incrementadas
            ResultSet key = ps.getGeneratedKeys();

            // La primera (y normalmente la unica) clave generada --> id de producto
            if (key.next()) {
                p.setIdProducto(key.getInt(1));
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when inserting Producto");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    public void insertMany(List<Producto> entity) {

        String sql = "INSERT INTO productos (nombre, descripcion, precio) VALUES (?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Producto producto : entity) {
                ps.setString(1, producto.getNombre());
                ps.setString(2, producto.getDescripcion());
                ps.setDouble(3, producto.getPrecio());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idProducto)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdProducto(keys.getInt(1));
                i++;
            }

            connection.setAutoCommit(true);

            // tira BatchException?

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DM) not established when inserting List<Producto>");
            System.err.println(sqle.getLocalizedMessage());
        }
    }


    public void insertManyMigra(List<Producto> entity) {

        String sql = "INSERT INTO productosMigra (nombre_migra, descripcion_migra, precio_migra, disponible_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnectionDmMigrada()) {

            // Transaccion atomica
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (Producto p : entity) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getDescripcion());
                ps.setDouble(3, p.getPrecio());
                ps.setBoolean(4, p.isDisponible());
                ps.setBoolean(5, p.isMigrado());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idCliente)
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada cliente (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdProducto(keys.getInt(1));
                i++;
            }

            connection.setAutoCommit(true);

            // tira BatchException?

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DM) not established when inserting List<Producto) into productosMigra");
            System.err.println(sqle.getLocalizedMessage());
        }
    }

    @Override
    public void updateOne(Producto p) {

        // Recuerda cerrar comilla simple
        String sql = "UPDATE productos SET nombre = '" + p.getNombre() + "', " +
                " descripcion = '" + p.getDescripcion() + "', " +
                " precio = '" + p.getPrecio() + "', " +
                " disponible = " + p.isDisponible() +
                " WHERE idProducto = " + p.getIdProducto() + ";";

        // Comprobamos query
//        System.out.println(sql);

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            int rows = ps.executeUpdate();

            // ps.executeUpdate devuelve el numero de filas afectadas
            if (rows > 0) {
                getLogger().trace("[" + p.getIdProducto() + "] Producto actualizado.");
            } else {
                getLogger().warn("Producto no actualizado, check ID: " + p.getIdProducto());
            }


        } catch (SQLException e) {
            getLogger().error("Could not update info of Producto: " + p.getIdProducto());
        }
    }

    @Override
    public void deleteById(int id) {

        String sql = "DELETE FROM productos WHERE idProductos = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0){
                getLogger().trace("Eliminado producto "+ id);
            } else {
                getLogger().warn("No se eliminó producto "+ id);
            }

            } catch (SQLException sqle) {
            getLogger().error("Error al eliminar producto "+ id +
                    "\n"+ sqle.getLocalizedMessage());
        }
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM productos WHERE idProductos = ?";

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()) {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            // El result set sale directamente de la query, no funciona con executeUpdate!
            ResultSet rs = ps.executeQuery();

            // Si hay info, rs.next devuelve true, si no, no.
            if (rs.next()) { return true;}

        } catch (SQLException sqle) {
            getLogger().error("Error confirmando si existe producto " + id);
            System.err.println(sqle.getLocalizedMessage());
        }

        // Si ha llegado aqui es porque no hay info, exists=false.
         return false;
    }

    public List<Producto> findAll() {

        List<Producto> productos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM productos";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getBoolean("disponible"));
                p.setIdProducto(rs.getInt(1));

                productos.add(p);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Productos");
            System.err.println(sqle.getLocalizedMessage());
        }

        return productos;
    }


    public List<Producto> findAllMigra() {

        List<Producto> productos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnectionDmOriginal()) {
            String sql = "SELECT * FROM productos";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("nombre_migra"),
                        rs.getString("descripcion_migra"),
                        rs.getInt("precio_migra"),
                        rs.getBoolean("disponible_migra"));
                p.setMigrado(true);
                p.setIdProducto(rs.getInt(1)); // recuerda asignar id al crear!

                productos.add(p);
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR, "Connection(DriverM) not established when listing Clientes");
            System.err.println(sqle.getLocalizedMessage());
        }

        return productos;
    }




    public List<Producto> findByAttributes(String nombre, Double precio) {
        String sql = "SELECT * FROM productos WHERE nombre = ? AND precio = ?";

        List<Producto> encontrados = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnectionDmOriginal()){

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Producto p = new Producto(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getBoolean("disponible"));
                p.setIdProducto(rs.getInt(1));

                encontrados.add(p);
            }
        } catch (SQLException sqle) {
            getLogger().error("No se ha podido encontrar según: "+ nombre +" y "+ precio);
            System.err.println(sqle.getLocalizedMessage());
        }

        return encontrados;
    }


    public void loadMigra(List<Producto> entity, Connection connection) {

        String sql = "INSERT INTO productosMigra " +
                "(nombre_migra, descripcion_migra, precio_migra, disponible_migra, migrado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {

            // Transaccion atomica se ejecuta en JdbcMigrator.migrarDb()
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            for (Producto p : entity) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getDescripcion());
                ps.setDouble(3, p.getPrecio());
                ps.setBoolean(4, p.isDisponible());
                ps.setBoolean(5, p.isMigrado());
                ps.addBatch();
            }

            ps.executeBatch();

            // Recuperamos clave generada (idProducto)S
            ResultSet keys = ps.getGeneratedKeys();

            // Asignamos id a cada producto (cuidado: SQL empieza keys en 1, no en 0 !!)
            int i = 0;
            while (i < entity.size() && keys.next()) {
                entity.get(i).setIdProducto(keys.getInt(1));
                i++;
            }

        } catch (SQLException sqle) {
            getLogger().log(LogLevel.ERROR,
                    "Connection(DM) not established when loading: "+ sqle.getLocalizedMessage());
        }
    }

}
