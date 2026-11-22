
package com.infinitystation.MarcosDesarrolloWeb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.infinitystation.MarcosDesarrolloWeb.model.Rol;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.repository.PedidoRepository;
import com.infinitystation.MarcosDesarrolloWeb.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PedidoRepository pedidoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registra un usuario y cifra su contraseña antes de guardarlo.
    public boolean registrarUsuario(Usuario usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Rol.CLIENTE); // las cuentas creadas desde la web siempre son CLIENTE

        usuarioRepository.save(usuario);

        return true;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Busca un usuario por su identificador.
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Actualiza nombre y correo sin modificar la contraseña almacenada.
    public boolean actualizarUsuario(Long id, String nombre, String correo) {
        if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
            return false;
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return false;
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuarioRepository.save(usuario);
        return true;
    }

    // Impide eliminar usuarios que tengan pedidos relacionados.
    public boolean eliminarUsuario(Long id) {
        if (pedidoRepository.existsByUsuario_Id(id)) {
            return false;
        }
        if (!usuarioRepository.existsById(id)) {
            return false;
        }

        usuarioRepository.deleteById(id);
        return true;
    }
}
