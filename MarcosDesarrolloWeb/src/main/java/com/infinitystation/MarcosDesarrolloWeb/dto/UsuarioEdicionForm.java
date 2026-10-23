package com.infinitystation.MarcosDesarrolloWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioEdicionForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Pattern(regexp = "^[\\p{L}\\p{M}]+(?:[ '-][\\p{L}\\p{M}]+)*\\s+[\\p{L}\\p{M}]+(?:[ '-][\\p{L}\\p{M}]+)*$",
            message = "Ingresa al menos un nombre y un apellido.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Ingresa un correo válido.")
    @Size(max = 300, message = "El correo no puede superar 300 caracteres.")
    private String correo;

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
}
