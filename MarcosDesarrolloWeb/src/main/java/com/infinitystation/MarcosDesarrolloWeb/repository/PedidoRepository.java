package com.infinitystation.MarcosDesarrolloWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto", "pago"})
    List<Pedido> findAll();

    // Busca el historial del usuario y carga los datos que necesita la vista.
    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto", "pago"})
    List<Pedido> findByUsuario_IdOrderByFechaPedidoDesc(Long usuarioId);

    // Comprueba si un usuario tiene pedidos antes de permitir eliminarlo.
    boolean existsByUsuario_Id(Long usuarioId);
}
