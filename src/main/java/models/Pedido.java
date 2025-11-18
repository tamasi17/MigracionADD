package main.java.models;
import java.sql.Date;

public class Pedido {

    private int idPedido;
    private Date fechaPedido;
    private double precio;
    private int clienteId;
    private int productoId;
    private boolean migrado;

    public Pedido(Date fechaPedido, double precio) {
        this.fechaPedido = fechaPedido;
        this.precio = precio;
    }

    public Pedido(Date fechaPedido, double precio, int clienteId, int productoId) {
        this.fechaPedido = fechaPedido;
        this.precio = precio;
        this.clienteId = clienteId;
        this.productoId = productoId;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public boolean isMigrado() {
        return migrado;
    }

    public void setMigrado(boolean migrado) {
        this.migrado = migrado;
    }

    @Override
    public String toString() {
        return "["+ idPedido +"] " + fechaPedido +
                "\n" + precio + "euros\n" +
                "Cliente: "+ clienteId +
                " - Producto: "+ productoId;
    }
}
