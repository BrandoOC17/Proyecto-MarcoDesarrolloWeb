package com.infinitystation.MarcosDesarrolloWeb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;

//permite guardar, consultar y buscar usuarios por su ID numérico.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Permite ver si ya esta creado unc orreo
    boolean existsByCorreo(String correo);
}
