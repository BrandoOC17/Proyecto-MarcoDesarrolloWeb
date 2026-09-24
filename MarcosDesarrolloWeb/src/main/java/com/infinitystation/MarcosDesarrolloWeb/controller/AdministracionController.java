package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import com.infinitystation.MarcosDesarrolloWeb.service.PedidoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdministracionController {

    private final PedidoService pedidoService;

    public AdministracionController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/administracion")
    public String administracion(Model model) {

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

        // Calcular ingresos de los pedidos completados
        BigDecimal ingresosMes = pedidos.stream()
                .filter(p -> "COMPLETADO".equalsIgnoreCase(p.getEstado()))
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
}