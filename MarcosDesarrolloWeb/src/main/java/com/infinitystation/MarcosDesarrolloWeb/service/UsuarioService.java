
package com.infinitystation.MarcosDesarrolloWeb.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean registrarUsuario(Usuario usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        usuarioRepository.save(usuario);

        return true;
    }

    public boolean validarUsuario(String correo, String contraseña) {

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equals(correo)
                    && passwordEncoder.matches(contraseña, usuario.getPassword())) {
                return true;
            }
        }

        return false;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
