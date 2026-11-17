package com.repo.pairazaman.controller;

import com.repo.pairazaman.model.Camara;
import com.repo.pairazaman.service.CamaraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/camaras")
public class CamaraController {

    private final CamaraService camaraService;

    public CamaraController(CamaraService camaraService) {
        this.camaraService = camaraService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("camaras", camaraService.listarTodas());
        model.addAttribute("totalActivas", camaraService.contarActivas());
        return "camaras/listar";
    }

    @GetMapping("/nueva")
    public String nuevaCamara(Model model) {
        model.addAttribute("camara", new Camara());
        model.addAttribute("tituloVista", "Registrar Nueva Cámara");
        return "camaras/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("camara") Camara camara,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            camaraService.guardar(camara);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cámara guardada correctamente.");
            return "redirect:/camaras";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            model.addAttribute("tituloVista", camara.getId() == null ? "Registrar Nueva Cámara" : "Editar Cámara");
            return "camaras/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            camaraService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cámara eliminada con éxito.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar la cámara si tiene incidencias asociadas.");
        }
        return "redirect:/camaras";
    }
}
