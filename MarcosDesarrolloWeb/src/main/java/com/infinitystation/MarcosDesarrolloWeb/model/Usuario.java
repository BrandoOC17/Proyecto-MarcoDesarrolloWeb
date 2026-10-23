package com.infinitystation.MarcosDesarrolloWeb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
// Importa la anotación que indica que la clase será una entidad gestionada por JPA.
import jakarta.persistence.Entity;
// Importa la anotación que identifica la clave primaria de la entidad, nombre tabla y relacion atributo columna.
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "usuario")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio.")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '-][\\p{L}\\p{M}]+)*\\s+[\\p{L}\\p{M}]+(?:[ '-][\\p{L}\\p{M}]+)*$",
            message = "Ingresa al menos un nombre y un apellido.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @Column(name = "correo", nullable = false, unique = true, length = 300)
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Ingresa un correo válido.")
    @Size(max = 300, message = "El correo no puede superar 300 caracteres.")
    private String correo;

    @Column(name = "contraseña", nullable = false, length = 255)
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(max = 255, message = "La contraseña codificada no puede superar 255 caracteres.")
    private String contraseña;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String contraseña) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return contraseña;
    }

    public void setPassword(String contraseña) {
        this.contraseña = contraseña;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }
}