package com.infinitystation.MarcosDesarrolloWeb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Validator;

import com.infinitystation.MarcosDesarrolloWeb.controller.PaginaController;
import com.infinitystation.MarcosDesarrolloWeb.controller.ProductoController;
import com.infinitystation.MarcosDesarrolloWeb.controller.UsuarioController;
import com.infinitystation.MarcosDesarrolloWeb.dto.CompraRequest;
import com.infinitystation.MarcosDesarrolloWeb.dto.CompraRequest.ProductoCarrito;
import com.infinitystation.MarcosDesarrolloWeb.dto.RegistroUsuarioForm;
import com.infinitystation.MarcosDesarrolloWeb.dto.UsuarioEdicionForm;
import com.infinitystation.MarcosDesarrolloWeb.model.Pedido;
import com.infinitystation.MarcosDesarrolloWeb.model.Rol;
import com.infinitystation.MarcosDesarrolloWeb.model.Producto;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.repository.ProductoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.UsuarioRepository;
import com.infinitystation.MarcosDesarrolloWeb.security.UsuarioPrincipal;
import com.infinitystation.MarcosDesarrolloWeb.service.PedidoService;
import com.infinitystation.MarcosDesarrolloWeb.service.UsuarioService;

@SpringBootTest
@Transactional
class PedidoCompraIntegrationTests {

    @Autowired
    private PaginaController paginaController;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProductoController productoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private Validator validator;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Comprueba una compra de varios productos y que el historial quede aislado por usuario.
    @Test
    void guardaCompraConDosProductosYLaAsociaAlUsuario() {
        Usuario usuario = new Usuario(
                "Usuario de prueba",
                "compra-" + UUID.randomUUID() + "@example.test",
                "password");
        usuario = usuarioRepository.saveAndFlush(usuario);

        Usuario otroUsuario = new Usuario(
                "Otro usuario de prueba",
                "compra-" + UUID.randomUUID() + "@example.test",
                "password");
        otroUsuario = usuarioRepository.saveAndFlush(otroUsuario);

        List<Producto> productos = productoRepository.findAll().stream()
                .limit(2)
                .toList();
        assertEquals(2, productos.size());

        CompraRequest solicitud = new CompraRequest(
                "Yape",
                UUID.randomUUID().toString(),
                List.of(
                        new ProductoCarrito(productos.get(0).getId(), 2),
                        new ProductoCarrito(productos.get(1).getId(), 1)));

        MockHttpSession session = new MockHttpSession();
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);

        var primeraRespuesta = paginaController.confirmarCompra(solicitud, principal, session);
        var segundaRespuesta = paginaController.confirmarCompra(solicitud, principal, session);
        assertEquals(HttpStatus.OK, primeraRespuesta.getStatusCode());
        assertEquals(HttpStatus.OK, segundaRespuesta.getStatusCode());
        assertEquals(true, segundaRespuesta.getBody().get("duplicada"));
        assertEquals(primeraRespuesta.getBody().get("pedidoId"), segundaRespuesta.getBody().get("pedidoId"));

        Long pedidoId = (Long) primeraRespuesta.getBody().get("pedidoId");
        assertNotNull(pedidoId);
        assertEquals(productos.get(0).getPrecio().multiply(BigDecimal.valueOf(2))
                .add(productos.get(1).getPrecio()), new BigDecimal((String) primeraRespuesta.getBody().get("total")));

        entityManager.clear();
        List<Pedido> historial = pedidoService.listarPedidosUsuario(usuario.getId());
        assertEquals(1, historial.size());
        assertTrue(historial.stream().anyMatch(compra -> compra.getId().equals(pedidoId)));
        assertTrue(pedidoService.listarPedidosUsuario(otroUsuario.getId()).isEmpty());

        Pedido compraGuardada = historial.stream()
                .filter(compra -> compra.getId().equals(pedidoId))
                .findFirst()
                .orElseThrow();
        assertEquals(2, compraGuardada.getDetalles().size());
        assertNotNull(compraGuardada.getFechaPedido());
        assertEquals("PENDIENTE", compraGuardada.getEstado());
        assertEquals(productos.get(0).getNombre(), compraGuardada.getDetalles().stream()
                .filter(detalle -> detalle.getProducto().getId().equals(productos.get(0).getId()))
                .findFirst()
                .orElseThrow()
                .getProducto()
                .getNombre());
        assertEquals(2, compraGuardada.getDetalles().stream()
                .filter(detalle -> detalle.getProducto().getId().equals(productos.get(0).getId()))
                .findFirst()
                .orElseThrow()
                .getCantidad());
        assertNotNull(compraGuardada.getPago());
        assertEquals("Yape", compraGuardada.getPago().getMetodoPago());
        assertNotNull(compraGuardada.getPago().getFechaPago());
        assertEquals(compraGuardada.getTotal(), compraGuardada.getPago().getMonto());
    }

    // Comprueba los CRUD administrativos y la validación de formularios (el acceso por rol lo aplica SecurityConfig).
    @Test
    void permiteGestionarProductosYUsuarios() {
        RegistroUsuarioForm nombreIncompleto = new RegistroUsuarioForm();
        nombreIncompleto.setNombre("María");
        assertTrue(validator.validate(nombreIncompleto).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("nombre")));

        RegistroUsuarioForm nombreCompleto = new RegistroUsuarioForm();
        nombreCompleto.setNombre("Ana María García");
        assertFalse(validator.validate(nombreCompleto).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("nombre")));

        assertEquals("productos-admin",
                productoController.administrarProductos(new ExtendedModelMap()));
        assertEquals("usuarios-admin",
                usuarioController.administrarUsuarios(new ExtendedModelMap()));

        Producto productoInvalido = new Producto();
        productoInvalido.setNombre("Producto inválido");
        productoInvalido.setPrecio(BigDecimal.ZERO);
        BeanPropertyBindingResult erroresProducto =
                new BeanPropertyBindingResult(productoInvalido, "producto");
        validator.validate(productoInvalido).forEach(error ->
                erroresProducto.rejectValue(error.getPropertyPath().toString(), error.getMessage()));
        assertEquals("producto-form", productoController.guardarProducto(
                productoInvalido, erroresProducto, new RedirectAttributesModelMap()));
        assertTrue(erroresProducto.hasErrors());

        String nombreProducto = "Producto CRUD " + UUID.randomUUID();
        Producto productoNuevo = new Producto(nombreProducto, new BigDecimal("12.50"),
                "PS5", "/img/astrobot.png");
        BeanPropertyBindingResult erroresProductoNuevo =
                new BeanPropertyBindingResult(productoNuevo, "producto");
        validator.validate(productoNuevo).forEach(error ->
                erroresProductoNuevo.rejectValue(error.getPropertyPath().toString(), error.getMessage()));
        assertEquals("redirect:/administracion/productos", productoController.guardarProducto(
                productoNuevo, erroresProductoNuevo, new RedirectAttributesModelMap()));

        Producto producto = productoRepository.findAll().stream()
                .filter(item -> nombreProducto.equals(item.getNombre()))
                .findFirst()
                .orElseThrow();
        producto.setNombre(nombreProducto + " editado");
        producto.setPrecio(new BigDecimal("15.25"));
        BeanPropertyBindingResult erroresEdicionProducto =
                new BeanPropertyBindingResult(producto, "producto");
        validator.validate(producto).forEach(error ->
                erroresEdicionProducto.rejectValue(error.getPropertyPath().toString(), error.getMessage()));
        productoController.guardarProducto(
                producto, erroresEdicionProducto, new RedirectAttributesModelMap());
        assertEquals(nombreProducto + " editado",
                productoRepository.findById(producto.getId()).orElseThrow().getNombre());

        productoController.eliminarProducto(
                producto.getId(), new RedirectAttributesModelMap());
        assertTrue(productoRepository.findById(producto.getId()).isEmpty());

        String correo = "crud-" + UUID.randomUUID() + "@example.test";
        RegistroUsuarioForm registro = new RegistroUsuarioForm();
        registro.setNombre("Usuario CRUD");
        registro.setCorreo(correo);
        registro.setPassword("ClaveSegura123");
        registro.setConfirmPassword("ClaveSegura123");
        BeanPropertyBindingResult erroresRegistro = new BeanPropertyBindingResult(registro, "registroForm");
        validator.validate(registro).forEach(error ->
                erroresRegistro.rejectValue(error.getPropertyPath().toString(), error.getMessage()));
        assertEquals("redirect:/administracion/usuarios", usuarioController.guardarUsuario(
                registro, erroresRegistro, new RedirectAttributesModelMap()));

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElseThrow();
        UsuarioEdicionForm edicion = new UsuarioEdicionForm();
        edicion.setNombre("Usuario CRUD editado");
        edicion.setCorreo(correo);
        BeanPropertyBindingResult erroresEdicionUsuario = new BeanPropertyBindingResult(edicion, "usuarioForm");
        validator.validate(edicion).forEach(error ->
                erroresEdicionUsuario.rejectValue(error.getPropertyPath().toString(), error.getMessage()));
        assertEquals("redirect:/administracion/usuarios", usuarioController.actualizarUsuario(
                usuario.getId(), edicion, erroresEdicionUsuario, new RedirectAttributesModelMap()));
        assertEquals("Usuario CRUD editado",
                usuarioRepository.findById(usuario.getId()).orElseThrow().getNombre());

        usuarioController.eliminarUsuario(usuario.getId(), new RedirectAttributesModelMap());
        assertTrue(usuarioRepository.findById(usuario.getId()).isEmpty());
    }

    // El registro crea siempre una cuenta CLIENTE y guarda la contraseña cifrada con BCrypt.
    @Test
    void registraClientesConClaveCifrada() {
        String correo = "rol-" + UUID.randomUUID() + "@example.test";
        Usuario nuevo = new Usuario("Cliente Seguro", correo, "ClaveSegura123");
        nuevo.setRol(Rol.ADMINISTRADOR); // un intento de escalar el rol desde fuera se ignora

        assertTrue(usuarioService.registrarUsuario(nuevo));

        Usuario guardado = usuarioRepository.findByCorreo(correo).orElseThrow();
        assertEquals(Rol.CLIENTE, guardado.getRol());
        assertTrue(passwordEncoder.matches("ClaveSegura123", guardado.getPassword()));
        assertFalse("ClaveSegura123".equals(guardado.getPassword()));
    }

    // La búsqueda del panel admin filtra por nombre del cliente o por número de pedido.
    @Test
    void filtraPedidosPorClienteOId() {
        Usuario usuario = usuarioRepository.saveAndFlush(new Usuario(
                "Buscable Prueba", "buscar-" + UUID.randomUUID() + "@example.test", "password"));
        Producto producto = productoRepository.findAll().get(0);
        Pedido pedido = pedidoService.registrarCompra(usuario.getId(), new CompraRequest(
                "Yape", UUID.randomUUID().toString(),
                List.of(new ProductoCarrito(producto.getId(), 1))));

        List<Pedido> todos = pedidoService.listarPedidos();
        assertEquals(todos.size(), pedidoService.filtrarPedidos(todos, "  ").size());
        assertTrue(pedidoService.filtrarPedidos(todos, "buscable").stream()
                .anyMatch(p -> p.getId().equals(pedido.getId())));
        assertEquals(1, pedidoService.filtrarPedidos(todos, "#" + pedido.getId()).size());
        assertTrue(pedidoService.filtrarPedidos(todos, "nombre-que-no-existe-xyz").isEmpty());
    }
}
