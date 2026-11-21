package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.UsuarioRegistroDto;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("mensajeError", "Usuario o contraseña incorrectos, o usuario inactivo.");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Cerraste sesión correctamente.");
        }
        return "auth/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuarioDto", new UsuarioRegistroDto());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuarioDto") UsuarioRegistroDto dto,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            model.addAttribute("mensajeError", "Las contraseñas no coinciden.");
            return "auth/registro";
        }

        try {
            usuarioService.registrar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario registrado exitosamente.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "auth/registro";
        }
    }
}