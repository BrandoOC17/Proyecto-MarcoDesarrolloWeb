package com.infinitystation.MarcosDesarrolloWeb.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.infinitystation.MarcosDesarrolloWeb.dto.CompraRequest;
import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.service.NoticiaService;
import com.infinitystation.MarcosDesarrolloWeb.service.PedidoService;
import com.infinitystation.MarcosDesarrolloWeb.service.ProductoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PaginaController {

    private final NoticiaService noticiaService;
    private final PedidoService pedidoService;
    private final ProductoService productoService;

    public PaginaController(
            NoticiaService noticiaService,
            PedidoService pedidoService,
            ProductoService productoService) {
        this.noticiaService = noticiaService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        Map<String, Producto> productosPorNombre = productoService.listarProductos().stream()
                .collect(Collectors.toMap(Producto::getNombre, producto -> producto, (primero, segundo) -> primero,
                        LinkedHashMap::new));
        model.addAttribute("productosPorNombre", productosPorNombre);
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
    public String compras(HttpSession session, Model model) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (!(usuarioId instanceof Long id)) {
            return "redirect:/login";
        }

        model.addAttribute("pedidos", pedidoService.listarPedidosUsuario(id));
        return "compras";
    }

    // Registra una compra para la cuenta de la sesión y evita repetir su confirmación.
    @PostMapping("/compras/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarCompra(
            @RequestBody CompraRequest solicitud,
            HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (!(usuarioId instanceof Long id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Inicia sesión para confirmar la compra."));
        }

        String confirmacionId = solicitud == null ? null : solicitud.confirmacionId();
        if (confirmacionId == null || confirmacionId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se recibió el identificador de confirmación."));
        }

        synchronized (session) {
            Object anteriorId = session.getAttribute("ultimaConfirmacionCompra");
            Object anteriorPedido = session.getAttribute("ultimoPedidoConfirmado");
            if (confirmacionId.equals(anteriorId) && anteriorPedido instanceof Long pedidoId) {
                Object anteriorTotal = session.getAttribute("ultimoPedidoTotal");
                return ResponseEntity.ok(Map.of(
                        "pedidoId", pedidoId,
                        "total", anteriorTotal,
                        "duplicada", true));
            }

            try {
                Pedido pedido = pedidoService.registrarCompra(id, solicitud);
                session.setAttribute("ultimaConfirmacionCompra", confirmacionId);
                session.setAttribute("ultimoPedidoConfirmado", pedido.getId());
                session.setAttribute("ultimoPedidoTotal", pedido.getTotal().toPlainString());

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("pedidoId", pedido.getId());
                respuesta.put("total", pedido.getTotal().toPlainString());
                respuesta.put("duplicada", false);
                return ResponseEntity.ok(respuesta);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
            }
        }
    }
}
