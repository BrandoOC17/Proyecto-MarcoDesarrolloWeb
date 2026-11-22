package com.infinitystation.MarcosDesarrolloWeb.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.infinitystation.MarcosDesarrolloWeb.model.Rol;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;

// Representa al usuario autenticado dentro de Spring Security (sesión web o JWT).
public class UsuarioPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String nombre;
    private final String correo;
    private final String password;
    private final Rol rol;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.password = usuario.getPassword();
        this.rol = usuario.getRol();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean esAdministrador() {
        return rol == Rol.ADMINISTRADOR;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    // El "username" de Spring Security es el correo.
    @Override
    public String getUsername() {
        return correo;
    }
}
