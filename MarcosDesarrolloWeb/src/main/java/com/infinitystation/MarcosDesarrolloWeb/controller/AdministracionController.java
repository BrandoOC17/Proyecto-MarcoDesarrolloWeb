package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import com.infinitystation.MarcosDesarrolloWeb.service.PedidoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdministracionController {

    private final PedidoService pedidoService;

    public AdministracionController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/administracion")
    public String administracion(Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("esAdministrador"))) {
            return "redirect:/login";
        }

        // Obtener todos los pedidos
        List<Pedido> pedidos = pedidoService.listarPedidos();

        // Cantidad de pedidos completados
        long pedidosCompletados = pedidos.stream()
                .filter(p -> "COMPLETADO".equalsIgnoreCase(p.getEstado()))
                .count();

        // Cantidad de pedidos pendientes
        long pedidosPendientes = pedidos.stream()
                .filter(p -> "PENDIENTE".equalsIgnoreCase(p.getEstado()))
                .count();

        // Ingresos del mes actual: solo pedidos completados de este mes
        YearMonth mesActual = YearMonth.now();
        BigDecimal ingresosMes = pedidos.stream()
                .filter(p -> "COMPLETADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> p.getFechaPedido() != null
                        && YearMonth.from(p.getFechaPedido()).equals(mesActual))
                .map(Pedido::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Enviar información a administracion.html
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("pedidosCompletados", pedidosCompletados);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("ingresosMes", ingresosMes);

        return "admin";
    }

    // Botón "check": pasa el pedido de PENDIENTE a COMPLETADO y recarga el panel
    @PostMapping("/administracion/pedidos/{id}/completar")
    public String completarPedido(@PathVariable Long id, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("esAdministrador"))) {
            return "redirect:/login";
        }

        pedidoService.completarPedido(id);
        return "redirect:/administracion";
    }
}