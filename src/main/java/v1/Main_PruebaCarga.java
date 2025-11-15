package main.java.v1;


import log4Mats.Logger;
import main.java.logging.LoggerProvider;
import main.java.models.Cliente;
import main.java.utils.ConnectionFactory;
import main.java.utils.DatabaseSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main_PruebaCarga {

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

        try (Connection c = ConnectionFactory.getConnectionDmOriginal()) {


            // Creamos tablas
            try {
                DatabaseSetup.crearTablaClientes();
            } catch (SQLException sqle) {
                LOGGER.error("Error generando la tabla clientes");
                sqle.getLocalizedMessage();
            }

            int size = 10;
            List<Cliente> clientes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                clientes.add(new Cliente("nombre" + i,
                        "apellido1." + i,
                        "apellido2." + i,
                        5100000 + i,
                        1000000 + i));
            }

            // Recurrimos a DAOs para tratar la base de datos
            DaoCliente_v1 daoClienteV1 = new DaoCliente_v1();


            // Insertamos batch
            daoClienteV1.insertMany(clientes);

            // Insertamos un cliente
            Cliente mats = new Cliente("Mati", "Eidelman", "Bokler",
                    67867867, 51090000);
            daoClienteV1.insertOne(mats);

            // Confirmamos que hemos recuperado generated keys correctamente
            for (Cliente cliente : clientes) {
                System.out.println(cliente.toString());
            }
            System.out.println(mats);

        } catch (SQLException sqle) {
            LOGGER.error("Error inserting Clientes batch");
            System.err.println(sqle.getLocalizedMessage());
        }




        /*
          // int size; cantidad de clientes
        // for() hasta size creando lista de clientes
        // crea otro cliente suelto para probar el insertOne

        //introducimos clientes
        // new clienteDao
        // clienteDao.insertMany(listaClientes)
        // clienteDao.insertOne(clienteSuelto)

        // same para pedidos y productos

        // comprueba datos estan bien, logica de negocio aqui,
        // se hace bien en mainEntregable

        // try (connection de Origen y connection de Destino)

        // clienteDAO y clienteDAO migrado

        // lista<ClienteOrigen> = clienteDao.findAll
        // lista<ClienteMigrado> = new ArrayList

        // for (client : listaClienteOrigen)
        //      listaClienteMigrado.add(new ClienteMigrado(0, client.getNombre, client.getApellido, etc)
        // clienteMigradoDao.insertMany(listaClienteMigrado)

         */


    }
}
