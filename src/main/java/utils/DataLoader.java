package main.java.utils;

import main.java.models.Cliente;
import main.java.v1.DaoClienteV1;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que se encarga de la carga inicial de datos
 * Recurre a DAO.
 * @author mati
 */
public class DataLoader {

    /**
     * Metodo que carga clientes (formato prac2) con DriverManager (v1)
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
        dao.loadMigra(migrados, connection);

        // Confirmamos que el batch se ha añadido bien en main con el logger

    }



}
