package com.infinitystation.MarcosDesarrolloWeb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;

// Vista de un pedido para la API REST (evita exponer las entidades JPA directamente).
public record PedidoApiDto(
        Long id,
        String cliente,
        String estado,
        BigDecimal total,
        LocalDateTime fecha,
        String metodoPago,
        List<Item> productos) {

    public record Item(String producto, String plataforma, int cantidad, BigDecimal precioUnitario) {
    }

    public static PedidoApiDto de(Pedido pedido) {
        List<Item> items = pedido.getDetalles().stream()
                .map(d -> new Item(
                        d.getProducto().getNombre(),
                        d.getProducto().getPlataforma(),
                        d.getCantidad(),
                        d.getPrecioUnitario()))
                .toList();
        return new PedidoApiDto(
                pedido.getId(),
                pedido.getCliente(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getFechaPedido(),
                pedido.getPago() == null ? null : pedido.getPago().getMetodoPago(),
                items);
    }
}
