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

        try {
            DatabaseSetup.crearTablaClientes();
        } catch (SQLException sqle) {
            LOGGER.error("Error generando la tabla clientes");
            sqle.getLocalizedMessage();
        }


    }
}
