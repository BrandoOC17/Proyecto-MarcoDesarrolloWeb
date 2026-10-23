package com.infinitystation.MarcosDesarrolloWeb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;

//permite guardar, consultar y buscar usuarios por su ID numérico.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Permite ver si ya esta creado unc orreo
    boolean existsByCorreo(String correo);

    // Busca una cuenta por su correo para iniciar sesión.
    Optional<Usuario> findByCorreo(String correo);

    // Comprueba si otro usuario ya utiliza el correo indicado.
    boolean existsByCorreoAndIdNot(String correo, Long id);
}
