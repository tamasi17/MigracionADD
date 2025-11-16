package main.java.dao;

import java.util.List;

public interface Dao<T> {

    T get(int id);
    void insertOne(T entity);
    void insertMany(List<T> entity);
    void updateOne(T entity);
    void deleteById (int id);
    boolean exists(T entity);
    List<T> findAll();
    List<T> findByAttribute(T filtro);
}
