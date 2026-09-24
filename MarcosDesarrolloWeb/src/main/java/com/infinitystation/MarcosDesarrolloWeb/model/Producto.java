package com.infinitystation.MarcosDesarrolloWeb.model;

public class Producto {

    private String id;
    private String nombre;
    private double precio;
    private String plataforma;
    private String imagen;

    public Producto() {
    }

    public Producto(String id, String nombre, double precio, String plataforma, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.plataforma = plataforma;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}