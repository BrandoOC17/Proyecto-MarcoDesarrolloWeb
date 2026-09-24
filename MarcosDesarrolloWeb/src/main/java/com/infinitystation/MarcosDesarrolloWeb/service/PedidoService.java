package com.infinitystation.MarcosDesarrolloWeb.service;

import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();

    public PedidoService() {

        Pedido pedido1 = new Pedido(
                "Carlos Ruiz",
                "God of War Ragnarok",
                new BigDecimal("249.99"),
                "Completado"
        );
        pedido1.setId(1L);
        pedido1.setPlataforma("PS5");
        pedido1.setCantidad(1);
        pedido1.setImagen("/img/god_of_war_ragnarok.png");

        Pedido pedido2 = new Pedido(
                "Ana Gómez",
                "The Last of Us Part II",
                new BigDecimal("180.00"),
                "Pendiente"
        );
        pedido2.setId(2L);
        pedido2.setPlataforma("PS4");
        pedido2.setCantidad(1);
        pedido2.setImagen("/img/the_last_of_us_part2.png");

        Pedido pedido3 = new Pedido(
                "Luis Torres",
                "Mando DualSense PS5",
                new BigDecimal("299.90"),
                "Pendiente"
        );
        pedido3.setId(3L);
        pedido3.setPlataforma("PS5");
        pedido3.setCantidad(1);
        pedido3.setImagen("/img/control_inalambrico_dualSense.png");

        Pedido pedido4 = new Pedido(
                "María Silva",
                "Console PlayStation 5 Slim",
                new BigDecimal("2399.00"),
                "Completado"
        );
        pedido4.setId(4L);
        pedido4.setPlataforma("PS5");
        pedido4.setCantidad(1);
        pedido4.setImagen("/img/play5.png");

        pedidos.add(pedido1);
        pedidos.add(pedido2);
        pedidos.add(pedido3);
        pedidos.add(pedido4);
    }

    // Obtener todos los pedidos
    public List<Pedido> listarPedidos() {
        return pedidos;
    }

    // Obtener pedidos completados
    public List<Pedido> listarPedidosCompletados() {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .toList();
    }

    // Obtener pedidos pendientes
    public List<Pedido> listarPedidosPendientes() {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Pendiente"))
                .toList();
    }

    // Cantidad de pedidos completados
    public long contarPedidosCompletados() {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .count();
    }

    // Cantidad de pedidos pendientes
    public long contarPedidosPendientes() {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Pendiente"))
                .count();
    }

    // Ingresos del mes
    public BigDecimal calcularIngresosMes() {
        return pedidos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}