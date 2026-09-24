package com.infinitystation.MarcosDesarrolloWeb.service;

import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public boolean registrarUsuario(Usuario usuario) {

        for (Usuario u : usuarios) {

            if (u.getCorreo().equals(usuario.getCorreo())) {
                return false;
            }
        }

        usuarios.add(usuario);

        return true;
    }

    public boolean validarUsuario(String correo, String password) {

        for (Usuario usuario : usuarios) {

            if (usuario.getCorreo().equals(correo)
                    && usuario.getPassword().equals(password)) {

                return true;
            }
        }

        return false;
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }
}