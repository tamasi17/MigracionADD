package main.java.utils;

import main.java.models.Cliente;
import main.java.models.Pedido;
import main.java.models.Producto;
import main.java.v1.DaoClienteV1;
import main.java.v1.DaoPedidoV1;
import main.java.v1.DaoProductoV1;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Random;

import static main.java.logging.LoggerProvider.getLogger;

/**
 * Clase que se encarga de la carga inicial de datos
 * Recurre a DAO.
 *
 * @author mati
 */
public class DataLoader {

    /**
     * Metodo que carga clientes (formato prac2) con DriverManager (v1)
     *
     * @param dao
     */
    public static void cargarClientes(DaoClienteV1 dao, int size) throws SQLException {


        List<Cliente> clientes = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            clientes.add(new Cliente("nombre" + i,
                    "apellido1." + i,
                    "apellido2." + i,
                    5100000 + i,
                    1000000 + i));
        }

        // Insertamos batch
        dao.insertMany(clientes);

        // Confirmamos que el batch se ha añadido bien
        // y hemos recuperado generated keys correctamente con logger en el main.

    }


    /**
     * Metodo que carga clientes (formato prac2migra) con DriverManager (v1)
     *
     * @param dao
     */
    public static void cargarClientesMigrada(DaoClienteV1 dao, List<Cliente> originales, Connection connection) throws SQLException {


        List<Cliente> migrados = new ArrayList<>();

        for (Cliente original : originales) {
            migrados.add(new Cliente(
                    original.getNombre(),
                    original.getApellido1(),
                    original.getDni(),
                    original.getTelefono(),
                    original.isActivo()
            ));
        }

        // Insertamos batch, transaccion atomica desde migrarDb()
        // No cerramos con resources dentro de loadMigra
        dao.loadClientesMigra(migrados, connection);

        // Confirmamos que el batch se ha añadido bien en main con el logger

    }


    /**
     * Metodo que carga productos (formato prac2) con DriverManager (v1)
     *
     * @param dao
     */
    public static void cargarProductos(DaoProductoV1 dao, int size) throws SQLException {


        List<Producto> productos = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            productos.add(new Producto("nombre" + i, "descripcion" + i, 10 + i));
        }

        // Insertamos batch
        dao.insertMany(productos);

        // Confirmamos que el batch se ha añadido bien
        // y hemos recuperado generated keys correctamente con logger en el main.
    }

    /**
     * Metodo que carga productos (formato prac2migra) con DriverManager (v1)
     *
     * @param daoPr
     */
    public static void cargarProductosMigrada(DaoProductoV1 daoPr, List<Producto> originales, Connection connection) throws SQLException {


        getLogger().trace("Cargando productosMigra. Prueba: " + originales.get(2).getNombre());

        List<Producto> migrados = new ArrayList<>();

        for (Producto original : originales) {
            migrados.add(new Producto(
                    original.getNombre(),
                    original.getDescripcion(),
                    original.getPrecio(),
                    original.isDisponible()
            ));
        }

        getLogger().trace("Preparado para loadProductosMigra. Prueba: "+ migrados.get(2).getNombre());


        // Insertamos batch, transaccion atomica desde migrarDb()
        // No cerramos con resources dentro de loadMigra
        daoPr.loadProductosMigra(migrados, connection);

        getLogger().trace("Load de productos finalizado");

    }



    /**
     * Metodo que carga pedidos (formato prac2) con DriverManager (v1)
     *
     * @param dao
     */
    public static void cargarPedidos(DaoPedidoV1 dao, DaoClienteV1 daoCli, DaoProductoV1 daoProd) throws SQLException {

        List<Producto> productos = daoProd.findAll();
        List<Cliente> clientes = daoCli.findAll();
        Random random = new Random();

        List<Pedido> pedidos = new ArrayList<>();
        for (int i = 1; i <= productos.size(); i++) {

            Cliente c = clientes.get(random.nextInt(clientes.size()));
            Producto pr = productos.get(random.nextInt(productos.size()));

            pedidos.add(new Pedido(
                    Date.valueOf(LocalDate.now()),
                    10 + i,
                    c.getIdCliente(),
                    pr.getIdProducto()));
        }

        // Insertamos batch
        dao.insertMany(pedidos);

        // Confirmamos que el batch se ha añadido bien
        // y hemos recuperado generated keys correctamente con logger en el main.
    }

    /**
     * Metodo que carga productos (formato prac2migra) con DriverManager (v1)
     *
     * @param dao
     */
    public static void cargarPedidosMigrada(DaoPedidoV1 dao, List<Pedido> originales, Connection connection) throws SQLException {

        getLogger().trace("Cargando pedidos en pedidosMigra");

        List<Pedido> migrados = new ArrayList<>();

        for (Pedido original : originales) {
            migrados.add(new Pedido(
                    original.getFechaPedido(),
                    original.getPrecio(),
                    original.getClienteId(),
                    original.getProductoId()
            ));
        }

        // Insertamos batch, transaccion atomica desde migrarDb()
        // No cerramos con resources dentro de loadMigra
        dao.loadMigra(migrados, connection);

        // Confirmamos que el batch se ha añadido bien en main con el logger

    }

}
