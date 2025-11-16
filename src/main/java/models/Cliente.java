package main.java.models;


import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellido1;
    private String apellido2; // no migrar
    private int dni;
    private int telefono;
    private boolean activo;
    private boolean migrado;
    private LocalDate fechaRegistro; // no migrar

    /**
     * Constructor Cliente para prac2
     * @param nombre
     * @param apellido1
     * @param apellido2
     * @param dni
     * @param telefono
     */
    public Cliente(String nombre, String apellido1, String apellido2, int dni, int telefono) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.dni = dni;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
        this.telefono = telefono;
    }

    /**
     * Constructor Cliente para prac2migra
     * @param nombre
     * @param apellido1
     * @param dni
     * @param telefono
     */
    public Cliente(String nombre, String apellido1, int dni, int telefono, boolean activo) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.dni = dni;
        this.telefono = telefono;
        this.activo = activo;
        this.migrado = true;
    }



    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() { return apellido1;}

    public void setApellido1(String apellido1) { this.apellido1 = apellido1;}

    public String getApellido2() {return apellido2;}

    public void setApellido2(String apellido2) {this.apellido2 = apellido2;}

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isMigrado() {
        return migrado;
    }

    public void setMigrado(boolean migrado) {
        this.migrado = migrado;
    }

    @Override
    public String toString() {
        return  "["+ idCliente + "] " + nombre + " " + apellido1 + " " + apellido2 +
                "\n DNI: " + dni + " - Tlf: " + telefono +
                "\n Activo: " + activo + " - FechaRegistro: " + fechaRegistro;
    }

    public String toStringMigra() {
        return  "["+ idCliente + "] " + nombre + " " + apellido1 +
                "\n DNI: " + dni + " - Tlf: " + telefono +
                "\n Activo: " + activo + " - Migrado: " + migrado;
    }

}
