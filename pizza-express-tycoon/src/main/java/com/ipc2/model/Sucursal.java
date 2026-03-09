package com.ipc2.model;

public class Sucursal {

    private int idSucursal;
    private String nombre;
    private String direccion;
    private boolean activa;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, String nombre, String direccion, boolean activa) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.activa = activa;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    @Override
public String toString() {
    return nombre;
}
}