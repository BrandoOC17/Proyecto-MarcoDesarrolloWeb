package com.infinitystation.MarcosDesarrolloWeb.dto;

import jakarta.validation.constraints.NotBlank;

// Cuerpo JSON de POST /api/auth/login.
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio.") String correo,
        @NotBlank(message = "La contraseña es obligatoria.") String password) {
}
