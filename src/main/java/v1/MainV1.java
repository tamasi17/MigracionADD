package main.java.v1;


import log4Mats.Logger;
import main.java.logging.LoggerProvider;
import main.java.utils.DbSetup;

import java.sql.SQLException;

public class MainV1 {

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

//
//        try {
//             DbSetup.crearTablaClientes();
//        } catch (SQLException sqle) {
//            LOGGER.error("Error generando la tabla clientes");
//            sqle.getLocalizedMessage();
//        }





    }
}
