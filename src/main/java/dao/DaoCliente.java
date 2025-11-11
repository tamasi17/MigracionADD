package main.java.dao;

import main.java.models.Cliente;

import java.util.List;

public interface DaoCliente {


    Cliente get();
    public void insert(Cliente c);
    void update(Cliente c);
    void deleteById (int id);
    boolean exists(Cliente c);
    List<Cliente> findAll();
    List<Cliente> findByName(String nombre);
}
