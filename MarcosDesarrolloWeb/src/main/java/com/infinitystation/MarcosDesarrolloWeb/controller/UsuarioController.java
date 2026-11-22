package com.infinitystation.MarcosDesarrolloWeb.controller;

import com.infinitystation.MarcosDesarrolloWeb.dto.RegistroUsuarioForm;
import com.infinitystation.MarcosDesarrolloWeb.dto.UsuarioEdicionForm;
import com.infinitystation.MarcosDesarrolloWeb.model.Rol;
import com.infinitystation.MarcosDesarrolloWeb.model.Usuario;
import com.infinitystation.MarcosDesarrolloWeb.service.UsuarioService;

import jakarta.validation.Valid;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// El inicio de sesión (POST /login) y el cierre (POST /salir) los procesa Spring Security.
// El acceso a /administracion/** lo restringe SecurityConfig (solo rol ADMINISTRADOR).
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("registroForm", new RegistroUsuarioForm());
        return "registro";
    }

    // Valida los datos del registro antes de guardar la cuenta (siempre con rol CLIENTE).
    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("registroForm") RegistroUsuarioForm formulario,
            BindingResult resultado) {

        // Comprueba que ambas contraseñas coincidan.
        if (!Objects.equals(formulario.getPassword(), formulario.getConfirmPassword())) {
            resultado.rejectValue("confirmPassword", "noCoincide", "Las contraseñas no coinciden.");
        }
        if (resultado.hasErrors()) {
            return "registro";
        }

        // Crea el usuario con los datos recibidos del formulario.
        Usuario usuario = new Usuario(formulario.getNombre(), formulario.getCorreo(), formulario.getPassword());

        // GUARDA AL USUARIO EN LA BD, SI YA EXISTE EL CORREO, REDIRIGE ERROR
        if (!usuarioService.registrarUsuario(usuario)) {
            resultado.rejectValue("correo", "correoExistente", "Ya existe una cuenta con ese correo.");
            return "registro";
        }

        return "redirect:/login?registro=exitoso";
    }

    // Muestra la lista de cuentas para el administrador.
    @GetMapping("/administracion/usuarios")
    public String administrarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios-admin";
    }

    // Abre el formulario administrativo para crear una cuenta.
    @GetMapping("/administracion/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("registroForm", new RegistroUsuarioForm());
        return "usuario-nuevo";
    }

    // Valida y registra una cuenta creada desde administración.
    @PostMapping("/administracion/usuarios/nuevo")
    public String guardarUsuario(
            @Valid @ModelAttribute("registroForm") RegistroUsuarioForm formulario,
            BindingResult resultado,
            RedirectAttributes attributes) {
        if (!Objects.equals(formulario.getPassword(), formulario.getConfirmPassword())) {
            resultado.rejectValue("confirmPassword", "noCoincide", "Las contraseñas no coinciden.");
        }
        if (resultado.hasErrors()) {
            return "usuario-nuevo";
        }

        Usuario usuario = new Usuario(formulario.getNombre(), formulario.getCorreo(), formulario.getPassword());
        if (!usuarioService.registrarUsuario(usuario)) {
            resultado.rejectValue("correo", "correoExistente", "Ya existe una cuenta con ese correo.");
            return "usuario-nuevo";
        }

        attributes.addFlashAttribute("mensaje", "El usuario se registró correctamente.");
        return "redirect:/administracion/usuarios";
    }

    // Abre el formulario con el nombre y correo de la cuenta seleccionada.
    @GetMapping("/administracion/usuarios/{id}/editar")
    public String editarUsuario(
            @PathVariable Long id,
            Model model,
            RedirectAttributes attributes) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        if (usuario == null) {
            attributes.addFlashAttribute("error", "No se encontró el usuario.");
            return "redirect:/administracion/usuarios";
        }

        UsuarioEdicionForm formulario = new UsuarioEdicionForm();
        formulario.setId(usuario.getId());
        formulario.setNombre(usuario.getNombre());
        formulario.setCorreo(usuario.getCorreo());
        model.addAttribute("usuarioForm", formulario);
        return "usuario-editar";
    }

    // Valida y actualiza el nombre y correo sin cambiar la contraseña.
    @PostMapping("/administracion/usuarios/{id}/editar")
    public String actualizarUsuario(
            @PathVariable Long id,
            @Valid @ModelAttribute("usuarioForm") UsuarioEdicionForm formulario,
            BindingResult resultado,
            RedirectAttributes attributes) {
        formulario.setId(id);
        if (resultado.hasErrors()) {
            return "usuario-editar";
        }

        if (!usuarioService.actualizarUsuario(id, formulario.getNombre(), formulario.getCorreo())) {
            resultado.rejectValue("correo", "correoExistente",
                    "El correo ya está registrado o el usuario ya no existe.");
            return "usuario-editar";
        }

        attributes.addFlashAttribute("mensaje", "El usuario se actualizó correctamente.");
        return "redirect:/administracion/usuarios";
    }

    // Elimina cuentas solo cuando no tienen pedidos asociados (y nunca una cuenta de administrador).
    @PostMapping("/administracion/usuarios/{id}/eliminar")
    public String eliminarUsuario(
            @PathVariable Long id,
            RedirectAttributes attributes) {

        Usuario objetivo = usuarioService.buscarPorId(id).orElse(null);
        if (objetivo != null && objetivo.getRol() == Rol.ADMINISTRADOR) {
            attributes.addFlashAttribute("error", "No se puede eliminar una cuenta de administrador.");
            return "redirect:/administracion/usuarios";
        }

        if (usuarioService.eliminarUsuario(id)) {
            attributes.addFlashAttribute("mensaje", "El usuario se eliminó correctamente.");
        } else {
            attributes.addFlashAttribute("error",
                    "No se puede eliminar un usuario con pedidos relacionados.");
        }
        return "redirect:/administracion/usuarios";
    }
}
