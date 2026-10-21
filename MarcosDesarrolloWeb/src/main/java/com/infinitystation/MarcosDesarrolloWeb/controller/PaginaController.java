package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.service.NoticiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    private final NoticiaService noticiaService;

    public PaginaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "pagina";
    }

    @GetMapping("/detalle")
    public String detalle() {
        return "detalle";
    }

    @GetMapping("/noticias")
    public String noticias(Model model) {
        model.addAttribute("noticias", noticiaService.listarNoticias());
        return "noticias";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }
    
    @GetMapping("/compras")
    public String compras() {
        return "compras";
    }
}
