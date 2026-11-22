package com.infinitystation.MarcosDesarrolloWeb.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Expone a todas las vistas Thymeleaf quién está autenticado (la navbar los usa para mostrar u ocultar enlaces).
@ControllerAdvice(basePackages = "com.infinitystation.MarcosDesarrolloWeb.controller")
public class SeguridadModelAdvice {

    @ModelAttribute("autenticado")
    public boolean autenticado() {
        return actual() != null;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin() {
        UsuarioPrincipal usuario = actual();
        return usuario != null && usuario.esAdministrador();
    }

    @ModelAttribute("esCliente")
    public boolean esCliente() {
        UsuarioPrincipal usuario = actual();
        return usuario != null && !usuario.esAdministrador();
    }

    @ModelAttribute("usuarioNombre")
    public String usuarioNombre() {
        UsuarioPrincipal usuario = actual();
        return usuario == null ? null : usuario.getNombre();
    }

    private UsuarioPrincipal actual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioPrincipal usuario) {
            return usuario;
        }
        return null;
    }
}
