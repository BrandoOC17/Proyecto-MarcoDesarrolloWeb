package com.infinitystation.MarcosDesarrolloWeb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import com.infinitystation.MarcosDesarrolloWeb.model.Rol;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.security.JwtService;
import com.infinitystation.MarcosDesarrolloWeb.security.UsuarioPrincipal;

// Prueba unitaria del flujo JWT (no necesita MySQL ni levantar Spring).
class JwtServiceTest {

    private static final String SECRETO = "clave-de-pruebas-con-mas-de-32-caracteres-123456";

    private UsuarioPrincipal usuario(Rol rol) {
        Usuario u = new Usuario("Cliente Prueba", "cliente@example.test", "hash");
        u.setId(7L);
        u.setRol(rol);
        return new UsuarioPrincipal(u);
    }

    @Test
    void generaTokenValidoConCorreoYRol() {
        JwtService servicio = new JwtService(SECRETO, 60);

        String token = servicio.generar(usuario(Rol.CLIENTE));
        Jwt jwt = servicio.validar(token);

        assertEquals("cliente@example.test", jwt.getSubject());
        assertEquals("CLIENTE", jwt.getClaimAsString("rol"));
        assertTrue(jwt.getExpiresAt().isAfter(jwt.getIssuedAt()));
    }

    @Test
    void rechazaTokenAlteradoOFirmadoConOtraClave() {
        JwtService servicio = new JwtService(SECRETO, 60);
        String token = servicio.generar(usuario(Rol.ADMINISTRADOR));

        assertThrows(JwtException.class, () -> servicio.validar(token + "x"));

        JwtService otro = new JwtService("otra-clave-distinta-con-mas-de-32-caracteres-999", 60);
        assertThrows(JwtException.class, () -> otro.validar(token));
    }

    @Test
    void rechazaTokenVencido() {
        JwtService servicio = new JwtService(SECRETO, -10); // vencido hace 10 minutos
        String token = servicio.generar(usuario(Rol.CLIENTE));

        assertThrows(JwtException.class, () -> servicio.validar(token));
    }

    @Test
    void exigeUnaClaveDeAlMenos32Caracteres() {
        assertThrows(IllegalStateException.class, () -> new JwtService("corta", 60));
    }
}
