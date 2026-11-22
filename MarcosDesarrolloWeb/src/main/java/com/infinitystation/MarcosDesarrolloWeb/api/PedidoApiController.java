package com.infinitystation.MarcosDesarrolloWeb.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infinitystation.MarcosDesarrolloWeb.dto.PedidoApiDto;
import com.infinitystation.MarcosDesarrolloWeb.security.UsuarioPrincipal;
import com.infinitystation.MarcosDesarrolloWeb.service.PedidoService;

// Endpoints protegidos con JWT:
//   GET /api/mis-pedidos   -> rol CLIENTE: solo sus propias compras
//   GET /api/admin/pedidos -> rol ADMINISTRADOR: todos los pedidos (?q= filtra por cliente o id)
@RestController
@RequestMapping("/api")
public class PedidoApiController {

    private final PedidoService pedidoService;

    public PedidoApiController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mis-pedidos")
    public List<PedidoApiDto> misPedidos(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return pedidoService.listarPedidosUsuario(usuario.getId()).stream()
                .map(PedidoApiDto::de)
                .toList();
    }

    @GetMapping("/admin/pedidos")
    public List<PedidoApiDto> todosLosPedidos(@RequestParam(name = "q", required = false) String busqueda) {
        return pedidoService.buscarPedidos(busqueda).stream()
                .map(PedidoApiDto::de)
                .toList();
    }
}
