package com.infinitystation.MarcosDesarrolloWeb.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @Column(name = "nombre", nullable = false, length = 150)
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres.")
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio debe ser mayor que cero.")
    private BigDecimal precio;

    @Column(name = "plataforma", length = 50)
    @Size(max = 50, message = "La plataforma no puede superar 50 caracteres.")
    private String plataforma;

    @Column(name = "imagen", columnDefinition = "TEXT")
    private String imagen;

    public Producto() {
    }

    public Producto(String nombre, BigDecimal precio, String plataforma, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.plataforma = plataforma;
        this.imagen = imagen;
    }

    public Producto(String nombre, double precio, String plataforma, String imagen) {
        this(nombre, BigDecimal.valueOf(precio), plataforma, imagen);
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
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