package main.java.interfaces;

import main.java.models.EstadoPedido;
import main.java.models.Pedido;

import java.util.List;

public interface DaoPedidos<T> extends Dao<Pedido> {

    List<T> findByAttributes(int idCliente, EstadoPedido estado);

}
