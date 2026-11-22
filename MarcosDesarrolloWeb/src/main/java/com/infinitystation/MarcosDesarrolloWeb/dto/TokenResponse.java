package com.infinitystation.MarcosDesarrolloWeb.dto;

// Respuesta de POST /api/auth/login: el token JWT y datos básicos de la sesión.
public record TokenResponse(
        String token,
        String tipo,
        long expiraEnSegundos,
        String correo,
        String rol) {
}
