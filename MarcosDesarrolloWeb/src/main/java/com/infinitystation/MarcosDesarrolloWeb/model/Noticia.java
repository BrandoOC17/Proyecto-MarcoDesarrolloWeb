package com.infinitystation.MarcosDesarrolloWeb.model;

public class Noticia {

    private int id;
    private String titulo;
    private String resumen;
    private String categoria;
    private String imagen;
    private String icono;

    public Noticia() {
    }

    public Noticia(int id, String titulo, String resumen, String categoria, String imagen, String icono) {
        this.id = id;
        this.titulo = titulo;
        this.resumen = resumen;
        this.categoria = categoria;
        this.imagen = imagen;
        this.icono = icono;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}
