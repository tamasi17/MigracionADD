package main.java.v1;

import main.java.models.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que se encarga de la carga inicial de datos
 * Recurre a DAO.
 * @author mati
 */
public class DataLoader {

    /**
     * Metodo que carga clientes con DriverManager (v1)
     * @param dao
     */
    public static void cargarClientes(DaoClienteV1 dao){

        int size = 7;
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
    }




}
