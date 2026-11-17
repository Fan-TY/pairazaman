package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.*;
import com.repo.pairazaman.service.CamaraService;
import com.repo.pairazaman.service.IncidenciaService;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;
    private final CamaraService camaraService;
    private final UsuarioService usuarioService;

    public IncidenciaController(IncidenciaService incidenciaService,
                                CamaraService camaraService,
                                UsuarioService usuarioService) {
        this.incidenciaService = incidenciaService;
        this.camaraService = camaraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoIncidencia estado,
                         @RequestParam(required = false) TipoIncidencia tipo,
                         Model model) {
        List<Incidencia> incidencias;
        if (estado != null || tipo != null) {
            incidencias = incidenciaService.filtrar(estado, tipo);
        } else {
            incidencias = incidenciaService.listarTodas();
        }

        model.addAttribute("incidencias", incidencias);
        model.addAttribute("estados", EstadoIncidencia.values());
        model.addAttribute("tipos", TipoIncidencia.values());
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("tipoSeleccionado", tipo);

        return "incidencias/listar";
    }

    @GetMapping("/nueva")
    public String nuevaIncidencia(Model model) {
        IncidenciaDto dto = new IncidenciaDto();
        dto.setFecha(LocalDate.now());
        dto.setHora(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        cargarAtributosFormulario(model, dto, "Registrar Nueva Incidencia", "/incidencias/guardar");
        return "incidencias/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("incidenciaDto") IncidenciaDto dto,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            Usuario operador = null;
            if (userDetails != null) {
                operador = usuarioService.buscarPorUsername(userDetails.getUsername()).orElse(null);
            }

            if (dto.getId() == null) {
                incidenciaService.registrar(dto, operador);
                redirectAttributes.addFlashAttribute("mensajeExito", "Incidencia registrada correctamente.");
            } else {
                incidenciaService.actualizar(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("mensajeExito", "Incidencia actualizada correctamente.");
            }
            return "redirect:/incidencias";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            cargarAtributosFormulario(model, dto,
                    dto.getId() == null ? "Registrar Nueva Incidencia" : "Editar Incidencia",
                    "/incidencias/guardar");
            return "incidencias/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return incidenciaService.buscarPorId(id)
                .map(incidencia -> {
                    IncidenciaDto dto = new IncidenciaDto();
                    dto.setId(incidencia.getId());
                    dto.setTitulo(incidencia.getTitulo());
                    dto.setDescripcion(incidencia.getDescripcion());
                    dto.setFecha(incidencia.getFecha());
                    dto.setHora(incidencia.getHora());
                    dto.setTipo(incidencia.getTipo());
                    dto.setPrioridad(incidencia.getPrioridad());
                    dto.setEstado(incidencia.getEstado());
                    dto.setUbicacion(incidencia.getUbicacion());
                    if (incidencia.getCamara() != null) {
                        dto.setCamaraId(incidencia.getCamara().getId());
                    }

                    cargarAtributosFormulario(model, dto, "Editar Incidencia #" + id, "/incidencias/guardar");
                    return "incidencias/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensajeError", "Incidencia no encontrada.");
                    return "redirect:/incidencias";
                });
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return incidenciaService.buscarPorId(id)
                .map(incidencia -> {
                    model.addAttribute("incidencia", incidencia);
                    model.addAttribute("estados", EstadoIncidencia.values());
                    return "incidencias/detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensajeError", "Incidencia no encontrada.");
                    return "redirect:/incidencias";
                });
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoIncidencia estado,
                                RedirectAttributes redirectAttributes) {
        try {
            incidenciaService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado a: " + estado.getEtiqueta());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar estado.");
        }
        return "redirect:/incidencias/detalle/" + id;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidenciaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Incidencia eliminada con éxito.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar la incidencia.");
        }
        return "redirect:/incidencias";
    }

    private void cargarAtributosFormulario(Model model, IncidenciaDto dto, String tituloVista, String actionUrl) {
        model.addAttribute("incidenciaDto", dto);
        model.addAttribute("tituloVista", tituloVista);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("tipos", TipoIncidencia.values());
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("estados", EstadoIncidencia.values());
        model.addAttribute("camaras", camaraService.listarTodas());
    }
}
