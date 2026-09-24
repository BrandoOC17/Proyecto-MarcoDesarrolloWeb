package com.infinitystation.MarcosDesarrolloWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/")
    public String inicio() {
        return "pagina";
    }

    @GetMapping("/detalle")
    public String detalle() {
        return "detalle";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }
}
