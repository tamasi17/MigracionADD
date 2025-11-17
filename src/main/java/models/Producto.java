package main.java.models;

/**
 * Clase que representa un producto
 * Se almacenara esta informacion en las tablas productos y productosMigra
 */
public class Producto {

    private int idProducto; // primary key
    private String nombre;
    private String descripcion;
    private double precio;
    private boolean disponible;
    private boolean migrado;

    /**
     * Constructor para tabla original productos, asigna disponible = true.
     * @param nombre
     * @param descripcion
     * @param precio
     */
    public Producto(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = true;
    }

    /**
     * Constructor para tabla migrada, recibe disponible de la original, asigna migrado = true.
     * @param nombre
     * @param descripcion
     * @param precio
     * @param disponible
     */
    public Producto( String nombre, String descripcion, double precio, boolean disponible) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.migrado = true;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isMigrado() {
        return migrado;
    }

    public void setMigrado(boolean migrado) {
        this.migrado = migrado;
    }
}
