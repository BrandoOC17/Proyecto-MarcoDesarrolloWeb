package com.infinitystation.MarcosDesarrolloWeb.model;

// Importa la anotación que indica que la clase será una entidad gestionada por JPA.
import jakarta.persistence.Entity;
// Importa la anotación que identifica la clave primaria de la entidad, nombre tabla y relacion atributo columna.
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

// Indica que esta clase representa una entidad que JPA puede guardar y consultar en la base de datos.
@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "plataforma")
    private String plataforma;

    @Column(name = "imagen")
    private String imagen;

    public Producto() {
    }

    public Producto(String nombre, double precio, String plataforma, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.plataforma = plataforma;
        this.imagen = imagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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