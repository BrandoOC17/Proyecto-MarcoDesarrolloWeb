package com.infinitystation.MarcosDesarrolloWeb.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

// Genera y valida tokens JWT firmados con HS256.
// La clave secreta sale de la variable de entorno JWT_SECRET (archivo .env), nunca del código.
@Service
public class JwtService {

    private static final String EMISOR = "infinity-station";

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long minutosExpiracion;

    public JwtService(
            @Value("${app.jwt.secret}") String secreto,
            @Value("${app.jwt.expiration-minutes:60}") long minutosExpiracion) {
        byte[] bytes = secreto == null ? new byte[0] : secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos 32 caracteres (256 bits) para firmar con HS256.");
        }
        SecretKey clave = new SecretKeySpec(bytes, "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(clave));
        this.decoder = NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
        this.minutosExpiracion = minutosExpiracion;
    }

    // Crea el token con el correo (subject), el rol y el id del usuario.
    public String generar(UsuarioPrincipal usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMISOR)
                .subject(usuario.getUsername())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(minutosExpiracion, ChronoUnit.MINUTES))
                .claim("rol", usuario.getRol().name())
                .claim("uid", usuario.getId())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }

    // Comprueba firma y vencimiento. Lanza JwtException si el token no es válido.
    public Jwt validar(String token) {
        return decoder.decode(token);
    }

    public long getSegundosExpiracion() {
        return minutosExpiracion * 60;
    }
}
