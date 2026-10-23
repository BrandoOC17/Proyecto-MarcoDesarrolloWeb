package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.service.ProductoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/videojuegos")
    public String videojuegos(Model model) {

        model.addAttribute("productos", productoService.listarProductos());

        return "videojuegos";
    }

    // Muestra el catálogo administrativo de productos.
    @GetMapping("/administracion/productos")
    public String administrarProductos(Model model, HttpSession session) {
        if (!esAdministrador(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.listarProductos());
        return "productos-admin";
    }

    // Abre un formulario vacío para registrar un producto.
    @GetMapping("/administracion/productos/nuevo")
    public String nuevoProducto(Model model, HttpSession session) {
        if (!esAdministrador(session)) {
            return "redirect:/login";
        }

        model.addAttribute("producto", new Producto());
        return "producto-form";
    }

    // Abre el formulario con los datos del producto seleccionado.
    @GetMapping("/administracion/productos/{id}/editar")
    public String editarProducto(
            @PathVariable Long id,
            Model model,
            HttpSession session,
            RedirectAttributes attributes) {
        if (!esAdministrador(session)) {
            return "redirect:/login";
        }

        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            attributes.addFlashAttribute("error", "No se encontró el producto.");
            return "redirect:/administracion/productos";
        }

        model.addAttribute("producto", producto);
        return "producto-form";
    }

    // Valida y guarda un producto nuevo o editado.
    @PostMapping("/administracion/productos/guardar")
    public String guardarProducto(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult resultado,
            HttpSession session,
            RedirectAttributes attributes) {
        if (!esAdministrador(session)) {
            return "redirect:/login";
        }
        if (resultado.hasErrors()) {
            return "producto-form";
        }

        productoService.guardarProducto(producto);
        attributes.addFlashAttribute("mensaje", "El producto se guardó correctamente.");
        return "redirect:/administracion/productos";
    }

    // Elimina un producto solo si no tiene detalles de pedidos asociados.
    @PostMapping("/administracion/productos/{id}/eliminar")
    public String eliminarProducto(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes attributes) {
        if (!esAdministrador(session)) {
            return "redirect:/login";
        }

        if (productoService.eliminarProducto(id)) {
            attributes.addFlashAttribute("mensaje", "El producto se eliminó correctamente.");
        } else {
            attributes.addFlashAttribute("error",
                    "No se puede eliminar un producto que aparece en pedidos existentes.");
        }
        return "redirect:/administracion/productos";
    }

    // Comprueba que la sesión pertenece al administrador.
    private boolean esAdministrador(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("esAdministrador"));
    }
}