package com.repo.pairazaman.controller;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;
import com.repo.pairazaman.service.PlanServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/planes")
public class PlanServicioController {

    private final PlanServicioService planService;

    public PlanServicioController(PlanServicioService planService) {
        this.planService = planService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) TipoPlan tipo, Model model) {
        model.addAttribute("planes", planService.listar(tipo));
        model.addAttribute("tipos", TipoPlan.values());
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("totalActivos", planService.contarActivos());
        return "planes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return formulario(new PlanServicio(), model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return planService.buscarPorId(id)
                .map(plan -> formulario(plan, model))
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "Plan no encontrado.");
                    return "redirect:/planes";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("plan") PlanServicio plan, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("mensajeError", "Revisa los números ingresados.");
            return formulario(plan, model);
        }
        try {
            boolean esNuevo = plan.getId() == null;
            planService.guardar(plan);
            redirect.addFlashAttribute("mensajeExito", esNuevo ? "Plan registrado correctamente." : "Plan actualizado correctamente.");
            return "redirect:/planes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return formulario(plan, model);
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            PlanServicio plan = planService.cambiarEstado(id);
            String estado = plan.isActivo() ? "activado" : "desactivado";
            redirect.addFlashAttribute("mensajeExito", "Plan " + plan.getNombre() + " " + estado + ".");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/planes";
    }

    private String formulario(PlanServicio plan, Model model) {
        model.addAttribute("plan", plan);
        model.addAttribute("tipos", TipoPlan.values());
        model.addAttribute("editando", plan.getId() != null);
        return "planes/form";
    }
}
