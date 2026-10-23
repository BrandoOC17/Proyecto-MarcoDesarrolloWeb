package com.infinitystation.MarcosDesarrolloWeb.dto;

import java.util.List;

public record CompraRequest(
        String metodoPago,
        String confirmacionId,
        List<ProductoCarrito> productos) {

    public record ProductoCarrito(Long productoId, Integer cantidad) {
    }
}
