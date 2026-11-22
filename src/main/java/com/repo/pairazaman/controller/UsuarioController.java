package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.UsuarioFormDto;
import com.repo.pairazaman.model.Rol;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuarioForm", new UsuarioFormDto());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("editando", false);
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuarioForm", UsuarioFormDto.desde(usuario));
                    model.addAttribute("roles", Rol.values());
                    model.addAttribute("editando", true);
                    return "usuarios/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensajeError", "Usuario no encontrado.");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuarioForm") UsuarioFormDto dto,
                          Principal principal,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            if (dto.getId() == null) {
                usuarioService.crear(dto);
                redirectAttributes.addFlashAttribute("mensajeExito", "Usuario creado correctamente.");
            } else {
                usuarioService.actualizar(dto, principal.getName());
                redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente.");
            }
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("editando", dto.getId() != null);
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarEstado(id, principal.getName());
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado del usuario actualizado.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/password")
    public String restablecerPassword(@PathVariable Long id,
                                      @RequestParam String nuevaPassword,
                                      RedirectAttributes redirectAttributes) {
        try {
            usuarioService.restablecerPassword(id, nuevaPassword);
            redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña restablecida correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}