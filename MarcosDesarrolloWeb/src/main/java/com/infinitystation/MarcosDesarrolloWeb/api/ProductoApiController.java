package com.infinitystation.MarcosDesarrolloWeb.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.service.ProductoService;

// Catálogo público de solo lectura (no requiere token).
@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final ProductoService productoService;

    public ProductoApiController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        return producto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(producto);
    }
}
