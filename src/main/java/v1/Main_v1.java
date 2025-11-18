package main.java.v1;


import log4Mats.Logger;
import main.java.interfaces.DaoPedidos;
import main.java.logging.LoggerProvider;
import main.java.models.Cliente;
import main.java.models.Pedido;
import main.java.models.Producto;
import main.java.models.ResultadoMigracion;
import main.java.utils.DataLoader;
import main.java.utils.DbSetup;
import main.java.utils.JdbcMigrator;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main_v1 {

    static final Logger LOGGER = LoggerProvider.getLogger();

    static final int CANTIDAD_CLIENTES = 12;
    static final int CANTIDAD_PRODUCTOS = 70;

    /**
     * Primero drop de todas las tablas para empezar de cero
     * DDL: Crear modelos bases de datos prac2 y prac2migra
     * Carga masiva datos en prac2
     * DML: Cambia datos, probar CRUD operations
     * Migracion datos prac2 a prac2 migra
     * Resultado en un fichero.
     */
    public static void main(String[] args) {


        LOGGER.setLogToConsole(true);

        /* En el workbench:
        CREATE DATABASE prac2;
        CREATE DATABASE prac2migra;

        CHECK : ConnectionFactory puerto 3306 o 3307
         */


        // Recurrimos a DAOs para tratar la base de datos
        DaoClienteV1 daoCli = new DaoClienteV1();
        DaoProductoV1 daoProd = new DaoProductoV1();
        DaoPedidoV1 daoPedi = new DaoPedidoV1();

        // =========== Drop, creado y carga de datos ============
        borrarTablas();
        crearTablas();
        cargaMasivaDatos(daoCli, daoProd, daoPedi);

        // =========== CRUDs ============
        crudClientes(daoCli);
        crudProductos(daoProd);
        crudPedidos(daoPedi);



        // ============= Nos preparamos para migracion =========
        int totalRegistros = daoCli.findAll().size() + daoProd.findAll().size() + daoPedi.findAll().size();
        LOGGER.info("Clientes en prac2 antes de migracion: " + daoCli.findAll().size() +
                "\nProductos en prac2 antes de migracion: " + daoProd.findAll().size() +
                "\nPedidos en prac2 antes de migracion: " + daoPedi.findAll().size() +
                "\nTotal: " + totalRegistros);



        // ===============  MIGRACION ===============

        LOGGER.trace("Creamos migrador con JDBC");
        JdbcMigrator migrator = new JdbcMigrator();
        ResultadoMigracion rm = migrator.migrarDB();
        LOGGER.info(rm.toString());

        System.out.println("Mostramos uno cualquiera: \n"
                + daoCli.getMigra(4).toStringMigra());


    }

    private static void crudClientes(DaoClienteV1 daoCli) {
        // Insertamos un cliente suelto y recuperamos su id
        Cliente mister = new Cliente(
                "Mati", "Eidelman", "Bokler",
                67867867, 51090000);
        int id = daoCli.insertOne(mister);

        // Cliente exists?
        LOGGER.trace("Comprobamos si Cliente suelto existe: " + daoCli.exists(id));

        // Update cliente
        mister.setNombre("Juan Manuel");
        mister.setApellido1("Fernandez");
        mister.setApellido2("Lopez");
        daoCli.updateOne(mister);
        LOGGER.trace("Cliente despues de update:\n" + daoCli.get(id));

        // Find all
        List<Cliente> encontrados = daoCli.findAll();
        LOGGER.trace("Usando findAll():\n" + encontrados.get(5)); // devolverá el cliente con idCliente 6.

        // Find by attributes (nombre y apellido)
        encontrados = daoCli.findByAttributes("Juan Manuel", "Fernandez");
        LOGGER.trace("\nUsando findByAttributes:\n" + encontrados);

        // DeleteById -- comentando estas líneas se puede ver el cliente suelto.
        int idBorrado = CANTIDAD_CLIENTES + 1; // borro el cliente suelto
        daoCli.deleteById(idBorrado);
        LOGGER.trace("\nBorrando...\n" +
                "Existe el cliente " + idBorrado + "? " + daoCli.exists(idBorrado));
    }

    private static void crudProductos(DaoProductoV1 daoProd) {
        // Insertamos un producto suelto y recuperamos su id
        Producto prod = new Producto(
                "Yate", "Gigante",
                304948.98, true);
        int id = daoProd.insertOne(prod);

        // Prod exists?
        LOGGER.trace("Comprobamos si prod existe: " + daoProd.exists(id));

        // Update producto
        prod.setNombre("Lancha");
        prod.setDescripcion("Chiquita");
        prod.setPrecio(9999.9);
        daoProd.updateOne(prod);
        LOGGER.trace("Producto despues de update:\n" + daoProd.get(id));

        // Find all
        List<Producto> encontrados = daoProd.findAll();
        LOGGER.trace("Usando findAll():\n" + encontrados.get(5)); // devolverá el prod con idProducto 6.

        // Find by attributes (nombre y apellido)
        encontrados = daoProd.findByAttributes("Lancha", 9999.9);
        LOGGER.trace("\nUsando findByAttributes:\n" + encontrados);

        // DeleteById -- comentando estas líneas se puede ver el cliente suelto.

        daoProd.deleteById(id); // borro el prod suelto
        LOGGER.trace("\nBorrando...\n" +
                "Existe el producto " + id + "? " + daoProd.exists(id));
    }

    private static void crudPedidos(DaoPedidoV1 daoPedidos) {
        // Insertamos un producto suelto y recuperamos su id
        Pedido pedido = new Pedido(
                Date.valueOf(LocalDate.now()), 239462.98,
                3, 4);
        int id = daoPedidos.insertOne(pedido);

        // Prod exists?
        LOGGER.trace("Comprobamos si pedido existe: " + daoPedidos.exists(id));

        // Update producto
        pedido.setClienteId(9);
        pedido.setProductoId(9);
        pedido.setPrecio(9999.9);
        daoPedidos.updateOne(pedido);
        LOGGER.trace("Pedido despues de update:\n" + daoPedidos.get(id));

        // Find all
        List<Pedido> encontrados = daoPedidos.findAll();
        LOGGER.trace("Usando findAll(), mostramos usuario 6:\n" + encontrados.get(5)); // devolverá el pedido con idPedido 6.

        // Find by attributes (clienteId y productoId)
        encontrados = daoPedidos.findByAttributes(9, 9);
        LOGGER.trace("\nUsando findByAttributes:\n" + encontrados);

        // DeleteById -- comentando estas líneas se puede ver el pedido suelto.
//        daoPedidos.deleteById(id); // borro el pedido suelto
        LOGGER.trace("\nBorrando...\n" +
                "Existe el cliente " + id + "? " + daoPedidos.exists(id));

    }

    private static void cargaMasivaDatos(DaoClienteV1 daoClienteV1, DaoProductoV1 daoProductoV1, DaoPedidoV1 daoPedidoV1) {
        // Cargamos las tablas Clientes, Productos y Pedidos en prac2
        // Pedidos tiene referencias a las anteriores asi que el Dao de cada una
        // nos da la informacion para generar los Pedidos
        try {
            DataLoader.cargarClientes(daoClienteV1, CANTIDAD_CLIENTES);
            DataLoader.cargarProductos(daoProductoV1, CANTIDAD_PRODUCTOS);
            DataLoader.cargarPedidos(daoPedidoV1, daoClienteV1, daoProductoV1);


            LOGGER.info("\nClientes cargados en prac2. Ejemplo: " + daoClienteV1.get(3).toString());
            LOGGER.info("\nProductos cargados en prac2. Ejemplo: " + daoProductoV1.get(3).toString());
            LOGGER.info("\nPedidos cargados en prac2. Ejemplo: " + daoPedidoV1.get(3).toString());

        } catch (SQLException e) {
            LOGGER.warn("No se pudieron cargar las tablas en prac2");
        }
    }

    private static void crearTablas() {
        // Creamos tablas
        try {
            DbSetup.crearTablaClientes();
            DbSetup.crearTablaClientesMigrada();
            DbSetup.crearTablaProductos();
            DbSetup.crearTablaProductosMigrada();
            DbSetup.crearTablaPedidos();
            DbSetup.crearTablaPedidosMigrada();
        } catch (SQLException sqle) {
            LOGGER.error("Error generando las tablas");
            sqle.getLocalizedMessage();
        }
    }

    private static void borrarTablas() {
        // Borramos tablas
        // Primero pedidos que incluye referencias a las otras
        DbSetup.borrarTabla("pedidos");
        DbSetup.borrarTablaMigrada("pedidosMigra");
        DbSetup.borrarTabla("clientes");
        DbSetup.borrarTablaMigrada("clientesMigra");
        DbSetup.borrarTabla("productos");
        DbSetup.borrarTablaMigrada("productosMigra");
    }
}
