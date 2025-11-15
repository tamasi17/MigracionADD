package main.java.v1;


import log4Mats.LogLevel;
import log4Mats.LogManager;
import log4Mats.Logger;
import main.java.logging.LoggerProvider;
import main.java.utils.DatabaseSetup;

import java.io.File;
import java.sql.SQLException;

public class Main_v1 {

        static final Logger LOGGER = LoggerProvider.getLogger();

    public static void main(String[] args) {


        /*
         Primero drop todas las tablas para empezar de cero
         DDL: Crear modelos bases de datos prac2 y prac2migra
         Carga masiva datos en prac2
         DML: Logica de negocio cambia datos
         Migracion datos prac2 a prac2 migra
         Resultado en un fichero.
         */

        LOGGER.setLogToConsole(true);

        // try conexiones origen y destino, statements origen y destino

        // instanciamos//llamamos a DBsetup, Daos necesarios

        // Creamos tablas: DBSetup + DataLoader de clientes, pedidos y productos

        // modificacionDB: CRUD de uno de cada

        // migracionDB: findAll devuelve Lista, forEach de la listaOrigen con listaMigrados.add(new Migrado)
        // insertMany(listaMigrados)

        // cerramos try conexiones


        try {
            DatabaseSetup.crearTablaClientes();
        } catch (SQLException sqle) {
            LOGGER.error("Error generando la tabla clientes");
            sqle.getLocalizedMessage();
        }

        /*

          try (
                Connection connOrigen = DBConfig.getConnectionOrigen();
                Connection connDestino = DBConfig.getConnectionDestino();
                Statement stmtOrigen = connOrigen.createStatement();
                Statement stmtDestino = connDestino.createStatement()
        ) {

            DBInitializer dbInitializer = new DBInitializer();
            int tamanoMaximo = 10;

            ClienteDAO clienteDAO = new ClienteDAOImpl(connOrigen);
            GenericDAO pedidoDAO = new PedidoDAOImpl(connOrigen);

            ClienteMigratedDAO clienteMigratedDAO = new ClienteMigraDAOImpl(connDestino);
            GenericMigratedDAO pedidosMigratedDAO = new PedidoMigraDAOImpl(connDestino);

            try {
                logger = LogManager.getLogger(Main.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            inicializadorDB(dbInitializer, stmtOrigen, connOrigen, stmtDestino, connDestino);
            dbInitializer.cargaMasivaOrigen(connOrigen, clienteDAO, pedidoDAO, tamanoMaximo);
            modificacionDB(clienteDAO, pedidoDAO);
            migracionDB(clienteDAO, pedidoDAO, clienteMigratedDAO, pedidosMigratedDAO);


        } catch (SQLException e) {
            logger.error("[LOGGER] Error en el proceso: " + e.getMessage());
        }
    }

         */







    }
}
