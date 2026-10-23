package com.infinitystation.MarcosDesarrolloWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infinitystation.MarcosDesarrolloWeb.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    // Comprueba si un producto aparece en pedidos existentes.
    boolean existsByProducto_Id(Long productoId);
}
