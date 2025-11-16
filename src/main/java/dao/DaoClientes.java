package main.java.dao;

import main.java.models.Cliente;

import java.util.List;

public interface DaoClientes<T> extends Dao<Cliente> {

    List<T> findByAttributes(String nombre, String apellido);

}
