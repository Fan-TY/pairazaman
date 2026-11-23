package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.TecnicoFormDto;
import com.repo.pairazaman.model.DisponibilidadTecnico;
import com.repo.pairazaman.service.TecnicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Registro y edición del personal técnico (datos, zona y disponibilidad).
 */
@Controller
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tecnicos", tecnicoService.listarTodos());
        model.addAttribute("disponibilidades", DisponibilidadTecnico.values());
        return "tecnicos/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new TecnicoFormDto(), "Registrar técnico");
        return "tecnicos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return tecnicoService.buscarPorId(id)
                .map(tecnico -> {
                    cargarFormulario(model, TecnicoFormDto.desde(tecnico), "Editar técnico");
                    return "tecnicos/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensajeError", "El técnico no existe.");
                    return "redirect:/tecnicos";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("tecnicoDto") TecnicoFormDto dto,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            boolean esNuevo = dto.getId() == null;
            tecnicoService.guardar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    esNuevo ? "Técnico registrado correctamente." : "Técnico actualizado correctamente.");
            return "redirect:/tecnicos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            cargarFormulario(model, dto, dto.getId() == null ? "Registrar técnico" : "Editar técnico");
            return "tecnicos/form";
        }
    }

    @PostMapping("/{id}/disponibilidad")
    public String cambiarDisponibilidad(@PathVariable Long id,
                                        @RequestParam DisponibilidadTecnico disponibilidad,
                                        RedirectAttributes redirectAttributes) {
        try {
            tecnicoService.cambiarDisponibilidad(id, disponibilidad);
            redirectAttributes.addFlashAttribute("mensajeExito", "Disponibilidad actualizada.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/tecnicos";
    }

    private void cargarFormulario(Model model, TecnicoFormDto dto, String titulo) {
        model.addAttribute("tecnicoDto", dto);
        model.addAttribute("tituloVista", titulo);
        model.addAttribute("disponibilidades", DisponibilidadTecnico.values());
    }
}
