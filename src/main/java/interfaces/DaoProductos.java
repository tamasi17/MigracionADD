package main.java.interfaces;

import main.java.models.EstadoPedido;
import main.java.models.Pedido;
import main.java.models.Producto;

import java.util.List;

public interface DaoProductos<Producto> extends Dao<Producto> {

    List<Producto> findByAttributes(String nombre, Double precio);

}
