package main.java.interfaces;

import main.java.models.Cliente;

import java.util.List;

public interface DaoClientes<T> extends Dao<Cliente> {

    List<Cliente> findByAttributes(String nombre, String apellido1);
}
