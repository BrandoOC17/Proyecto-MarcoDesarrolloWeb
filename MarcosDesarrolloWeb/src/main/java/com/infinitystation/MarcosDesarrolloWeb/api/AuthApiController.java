package com.infinitystation.MarcosDesarrolloWeb.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinitystation.MarcosDesarrolloWeb.dto.LoginRequest;
import com.infinitystation.MarcosDesarrolloWeb.dto.TokenResponse;
import com.infinitystation.MarcosDesarrolloWeb.security.JwtService;
import com.infinitystation.MarcosDesarrolloWeb.security.UsuarioPrincipal;

import jakarta.validation.Valid;

// Login de la API: Spring Security valida las credenciales y se entrega un JWT.
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest solicitud) {
        try {
            Authentication autenticacion = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(solicitud.correo(), solicitud.password()));
            UsuarioPrincipal usuario = (UsuarioPrincipal) autenticacion.getPrincipal();

            return ResponseEntity.ok(new TokenResponse(
                    jwtService.generar(usuario),
                    "Bearer",
                    jwtService.getSegundosExpiracion(),
                    usuario.getUsername(),
                    usuario.getRol().name()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo o contraseña incorrectos."));
        }
    }
}
