package main.java.v1;


import log4Mats.Logger;
import main.java.logging.LoggerProvider;
import main.java.models.Cliente;
import main.java.utils.DataLoader;
import main.java.utils.DbSetup;
import main.java.utils.JdbcMigrator;

import java.sql.SQLException;
import java.util.List;

public class Main_v1 {

    static final Logger LOGGER = LoggerProvider.getLogger();

    static final int CANTIDAD_CLIENTES = 12;
    static final int CANTIDAD_PEDIDOS = 70;
    static final int CANTIDAD_PRODUCTOS = 50;

    /**
        Primero drop de todas las tablas para empezar de cero
        DDL: Crear modelos bases de datos prac2 y prac2migra
        Carga masiva datos en prac2
        DML: Cambia datos, probar CRUD operations
        Migracion datos prac2 a prac2 migra
        Resultado en un fichero.
        */
    public static void main(String[] args) {



        LOGGER.setLogToConsole(true);

        /* En el workbench:
        CREATE DATABASE prac2;
        CREATE DATABASE prac2migra;

        CHECK : ConnectionFactory puerto 3306 o 3307
         */


        borrarTablas();

        crearTablas();

        // Recurrimos a DAOs para tratar la base de datos
        DaoClienteV1 daoClienteV1 = new DaoClienteV1();

        // Cargamos la tabla Clientes en prac2
        try {
            DataLoader.cargarClientes(daoClienteV1, CANTIDAD_CLIENTES);

            // continuar con productos y clientes !!!!

            LOGGER.info("Clientes cargados en prac2. Ejemplo: \n"+ daoClienteV1.get(3).toString());
        } catch (SQLException e) {
            LOGGER.warn("No se pudo cargar la tabla clientes en prac2");
        }

        // Insertamos un cliente suelto
        Cliente mister = new Cliente(
                "Mati", "Eidelman", "Bokler",
                67867867, 51090000);
        daoClienteV1.insertOne(mister);

        // Cliente exists?
        LOGGER.trace("Comprobamos si Cliente suelto existe: " + daoClienteV1.exists(mister.getIdCliente()));

        // Update cliente
        mister.setNombre("Juan Manuel");
        mister.setApellido1("Fernandez");
        mister.setApellido2("Lopez");
        daoClienteV1.updateOne(mister);
        LOGGER.trace("Cliente despues de update:\n" + daoClienteV1.get(8));

        // Find all
        List<Cliente> encontrados = daoClienteV1.findAll();
        LOGGER.trace("Usando findAll():\n" + encontrados.get(5)); // devolverá el cliente con idCliente 6.

        // Find by attributes (nombre y apellido)
        encontrados = daoClienteV1.findByAttributes("Juan Manuel", "Fernandez");
        LOGGER.trace("\nUsando findByAttributes:\n" + encontrados);

        // DeleteById -- comentando estas líneas se puede ver el cliente suelto.
        int idBorrado = CANTIDAD_CLIENTES+1; // borro el cliente suelto
        daoClienteV1.deleteById(idBorrado);
        LOGGER.trace("\nBorrando...\n" +
                "Existe el cliente " + idBorrado + "? " + daoClienteV1.exists(idBorrado));


        // Nos preparamos para migracion
        LOGGER.info("Clientes en prac2 antes de migracion: "+ daoClienteV1.findAll().size());


        // ===============  MIGRACION ===============

            LOGGER.trace("Creamos migrador con JDBC");
            JdbcMigrator migrator = new JdbcMigrator();
            migrator.migrarDB();

        LOGGER.info("Clientes en prac2migra después de migracion: "+ daoClienteV1.findAllMigra().size());

        System.out.println("Mostramos uno cualquiera: \n"
                + daoClienteV1.getMigra(4).toStringMigra());


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
        DbSetup.borrarTabla("clientes");
        DbSetup.borrarTablaMigrada("clientesMigra");
        DbSetup.borrarTabla("productos");
        DbSetup.borrarTablaMigrada("productosMigra");
        DbSetup.borrarTabla("pedidos");
        DbSetup.borrarTablaMigrada("pedidosMigra");
    }
}
