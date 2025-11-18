package main.java.interfaces;

import java.util.List;

public interface Dao<T> {

    T get(int id);

    int insertOne(T entity);

    void insertMany(List<T> entity);

    void updateOne(T entity);

    void deleteById(int id);

    boolean exists(int id);

    List<T> findAll();
}
