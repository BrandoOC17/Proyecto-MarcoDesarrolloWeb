package com.infinitystation.MarcosDesarrolloWeb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

// Dos cadenas de seguridad:
//  1) /api/**  -> REST sin sesión, protegida con JWT (Authorization: Bearer ...).
//  2) el resto -> aplicación web Thymeleaf con login por formulario, sesión y CSRF.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String CLIENTE = "CLIENTE";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuracion) throws Exception {
        return configuracion.getAuthenticationManager();
    }

    // ---------- API REST protegida con JWT ----------
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) throws Exception {

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable()) // sin sesión ni cookies: el token viaja en la cabecera
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(ADMIN)
                        .requestMatchers("/api/mis-pedidos").hasRole(CLIENTE)
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, denegado) ->
                                response.sendError(HttpStatus.FORBIDDEN.value())))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ---------- Aplicación web (Spring MVC + Thymeleaf) ----------
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        // La compra se confirma con fetch(): sin sesión debe responder 401 (no redirigir al login en HTML).
        RequestMatcher confirmarCompra = request -> "/compras/confirmar".equals(request.getServletPath());

        http
                .authorizeHttpRequests(auth -> auth
                        // recursos estáticos y páginas públicas
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/", "/videojuegos", "/detalle", "/noticias", "/contacto", "/carrito",
                                "/login", "/registro").permitAll()
                        // solo administrador: productos, usuarios y pedidos
                        .requestMatchers("/administracion/**").hasRole(ADMIN)
                        // solo cliente: comprar y ver sus propias compras
                        .requestMatchers("/compras", "/compras/**").hasRole(CLIENTE)
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("correo")
                        .passwordParameter("password")
                        .successHandler((request, response, autenticacion) -> {
                            boolean admin = autenticacion.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + ADMIN));
                            response.sendRedirect(request.getContextPath() + (admin ? "/administracion" : "/"));
                        })
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/salir") // con CSRF activo, el cierre de sesión debe ser POST
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), confirmarCompra));

        return http.build();
    }
}
