package com.repo.pairazaman.controller;

import com.repo.pairazaman.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/cuenta")
public class CuentaController {

    private final UsuarioService usuarioService;

    public CuentaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/password")
    public String formulario() {
        return "cuenta/password";
    }

    @PostMapping("/password")
    public String cambiar(@RequestParam String actual,
                          @RequestParam String nueva,
                          @RequestParam String confirmar,
                          Principal principal,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (!nueva.equals(confirmar)) {
            model.addAttribute("mensajeError", "Las contraseñas nuevas no coinciden.");
            return "cuenta/password";
        }
        try {
            usuarioService.cambiarPassword(principal.getName(), actual, nueva);
            redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña actualizada correctamente.");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "cuenta/password";
        }
    }
}