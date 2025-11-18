package main.java.interfaces;

import main.java.models.EstadoPedido;
import main.java.models.Pedido;

import java.util.List;

public interface DaoPedidos<Pedido> extends Dao<Pedido> {

    List<Pedido> findByAttributes(int clienteId, int productoId);
}
