package com.infinitystation.MarcosDesarrolloWeb.service;

import com.infinitystation.MarcosDesarrolloWeb.dto.CompraRequest;
import com.infinitystation.MarcosDesarrolloWeb.dto.CompraRequest.ProductoCarrito;
import com.infinitystation.MarcosDesarrolloWeb.model.DetallePedido;
import com.infinitystation.MarcosDesarrolloWeb.model.Pago;
import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.repository.DetallePedidoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.PagoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.PedidoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.ProductoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            PagoRepository pagoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.pagoRepository = pagoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los pedidos
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // Devuelve únicamente las compras asociadas al usuario autenticado.
    public List<Pedido> listarPedidosUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuario_IdOrderByFechaPedidoDesc(usuarioId);
    }

    // Guarda el pedido, sus detalles y el pago simulado dentro de una transacción.
    @Transactional
    public Pedido registrarCompra(Long usuarioId, CompraRequest solicitud) {
        if (solicitud == null
                || solicitud.productos() == null
                || solicitud.productos().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        if (!"Yape".equals(solicitud.metodoPago())
                && !"Tarjeta".equals(solicitud.metodoPago())) {
            throw new IllegalArgumentException("Selecciona Yape o Tarjeta.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario autenticado."));

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ProductoCarrito item : solicitud.productos()) {
            if (item == null || item.productoId() == null
                    || item.cantidad() == null || item.cantidad() < 1) {
                throw new IllegalArgumentException("El carrito contiene un producto inválido.");
            }

            int cantidad = cantidades.getOrDefault(item.productoId(), 0) + item.cantidad();
            if (cantidad > 10) {
                throw new IllegalArgumentException("La cantidad máxima por videojuego es 10.");
            }
            cantidades.put(item.productoId(), cantidad);
        }

        List<Producto> productos = productoRepository.findAllById(cantidades.keySet());
        if (productos.size() != cantidades.size()) {
            throw new IllegalArgumentException("Uno o más videojuegos ya no están disponibles.");
        }

        Map<Long, Producto> productosPorId = new LinkedHashMap<>();
        for (Producto producto : productos) {
            productosPorId.put(producto.getId(), producto);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> item : cantidades.entrySet()) {
            Producto producto = productosPorId.get(item.getKey());
            if (producto.getPrecio() == null) {
                throw new IllegalArgumentException("Uno de los videojuegos no tiene precio disponible.");
            }
            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.getValue())));
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");
        pedido = pedidoRepository.saveAndFlush(pedido);

        for (Map.Entry<Long, Integer> item : cantidades.entrySet()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(productosPorId.get(item.getKey()));
            detalle.setCantidad(item.getValue());
            detalle.setPrecioUnitario(productosPorId.get(item.getKey()).getPrecio());
            detallePedidoRepository.save(detalle);
        }

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(total);
        pago.setMetodoPago(solicitud.metodoPago());
        pago.setEstado("PENDIENTE");
        pagoRepository.save(pago);

        return pedido;
    }

    // Obtener pedidos completados
    public List<Pedido> listarPedidosCompletados() {
        return listarPedidos().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .toList();
    }

    // Obtener pedidos pendientes
    public List<Pedido> listarPedidosPendientes() {
        return listarPedidos().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Pendiente"))
                .toList();
    }

    // Cantidad de pedidos completados
    public long contarPedidosCompletados() {
        return listarPedidos().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .count();
    }

    // Cantidad de pedidos pendientes
    public long contarPedidosPendientes() {
        return listarPedidos().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Pendiente"))
                .count();
    }

    // Ingresos del mes
    public BigDecimal calcularIngresosMes() {
        return listarPedidos().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("Completado"))
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}