
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

        //credenciales del administrador.
        if ("adminInfinity@gmail.com".equals(correo)
                && "Keys2026".equals(password)) {
            return "redirect:/administracion";
        }

        // Valida las credenciales del usuario con mysql
        if (usuarioService.validarUsuario(correo, password)) {
            return "redirect:/";
        }

        return "redirect:/login?error";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
        //aqui requestparam recibe datos ojito cepre
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String confirmPassword) {

        // Comprueba que ambas contraseñas coincidan.
        if (!password.equals(confirmPassword)) {
            return "redirect:/registro?error=password";
        }

        // Crea el usuario con los datos recibidos del formulario.
        Usuario usuario = new Usuario(nombre, correo, password);

        // GUARDA AL USUARIO EN LA BD, SI YA EXISTE EL CORREO, REDIRIGE ERROR
        if (!usuarioService.registrarUsuario(usuario)) {
            return "redirect:/registro?error=correo";
        }

        return "redirect:/login?registro=exitoso";
    }
}
