package main.java.dao;

import main.java.models.Cliente;

import java.util.List;

public interface Dao<Cliente> {


    
    

    Cliente get();
    public void insert(Cliente c);
    void update();
    void deleteById ();
    boolean exists();
    List<Cliente> findAll();
    List<Cliente> findByAttributes();
}
