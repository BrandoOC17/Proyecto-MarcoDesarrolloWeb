package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.service.UsuarioService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String password) {

        // 1. Validar si es el administrador único
        if ("adminInfinity@gmail.com".equals(correo) && "Keys2026".equals(password)) {
            return "redirect:/administracion";
        }

        // 2. Si es un usuario normal, validar mediante el servicio
        if (usuarioService.validarUsuario(correo, password)) {
            return "redirect:/";
        }

        // Si falla la validación, recarga el login
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            return "registro";
        }

        Usuario usuario = new Usuario(nombre, correo, password);

        if (!usuarioService.registrarUsuario(usuario)) {
            return "registro";
        }

        return "redirect:/login";
    }
}
