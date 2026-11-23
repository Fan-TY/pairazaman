package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.DiaAgenda;
import com.repo.pairazaman.dto.OrdenInstalacionDto;
import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.model.EstadoOrden;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.model.TipoOrden;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.service.InstalacionService;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Programación de instalaciones y personal técnico (RF-03 / ECU-03).
 * Uso: Secretaria (y Gerente). El técnico solo puede ver el listado de sus propias órdenes.
 */
@Controller
@RequestMapping("/instalaciones")
public class InstalacionController {

    private final InstalacionService instalacionService;
    private final UsuarioService usuarioService;

    public InstalacionController(InstalacionService instalacionService, UsuarioService usuarioService) {
        this.instalacionService = instalacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoOrden estado,
                         @RequestParam(required = false) Long tecnicoId,
                         Authentication authentication,
                         Model model) {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        boolean soloLectura = roles.contains("ROLE_TECNICO")
                && !roles.contains("ROLE_GERENTE") && !roles.contains("ROLE_SECRETARIA");

        if (soloLectura) {
            // Un técnico solo ve sus propias órdenes.
            tecnicoId = usuarioService.buscarPorUsername(authentication.getName())
                    .map(Usuario::getTecnico)
                    .map(Tecnico::getId)
                    .orElse(-1L);
        }

        List<Instalacion> ordenes = instalacionService.listar(estado, tecnicoId);

        model.addAttribute("ordenes", ordenes);
        model.addAttribute("estados", EstadoOrden.values());
        model.addAttribute("tecnicos", instalacionService.tecnicosParaAsignar());
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("tecnicoSeleccionado", soloLectura ? null : tecnicoId);
        model.addAttribute("soloLectura", soloLectura);
        return "instalaciones/listar";
    }

    @GetMapping("/agenda")
    public String agenda(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         @RequestParam(required = false) Long tecnicoId,
                         Model model) {
        LocalDate referencia = fecha != null ? fecha : LocalDate.now();
        List<DiaAgenda> semana = instalacionService.agendaSemanal(referencia, tecnicoId);
        LocalDate lunes = semana.get(0).getFecha();

        model.addAttribute("semana", semana);
        model.addAttribute("lunes", lunes);
        model.addAttribute("domingo", lunes.plusDays(6));
        model.addAttribute("semanaAnterior", lunes.minusWeeks(1));
        model.addAttribute("semanaSiguiente", lunes.plusWeeks(1));
        model.addAttribute("tecnicos", instalacionService.tecnicosParaAsignar());
        model.addAttribute("tecnicoSeleccionado", tecnicoId);
        return "instalaciones/agenda";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarFormulario(model, new OrdenInstalacionDto(), "Programar nueva orden", false);
        return "instalaciones/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return instalacionService.buscarPorId(id)
                .map(orden -> {
                    cargarFormulario(model, OrdenInstalacionDto.desde(orden), "Editar orden #" + orden.getId(), true);
                    return "instalaciones/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensajeError", "La orden no existe.");
                    return "redirect:/instalaciones";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("ordenDto") OrdenInstalacionDto dto,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            boolean esNueva = dto.getId() == null;
            Instalacion orden = instalacionService.guardar(dto);

            String mensaje = esNueva ? "Orden programada correctamente." : "Orden actualizada correctamente.";
            if (orden.getTecnico() != null) {
                mensaje += " Técnico asignado: " + orden.getTecnico().getNombreCompleto() + ".";
            } else {
                mensaje += " Aún no tiene técnico asignado.";
            }
            redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
            return "redirect:/instalaciones";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            cargarFormulario(model, dto,
                    dto.getId() == null ? "Programar nueva orden" : "Editar orden #" + dto.getId(),
                    dto.getId() != null);
            return "instalaciones/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoOrden estado,
                                RedirectAttributes redirectAttributes) {
        try {
            instalacionService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Orden #" + id + " marcada como " + estado.getEtiqueta() + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/instalaciones";
    }

    private void cargarFormulario(Model model, OrdenInstalacionDto dto, String titulo, boolean editando) {
        model.addAttribute("ordenDto", dto);
        model.addAttribute("tituloVista", titulo);
        model.addAttribute("editando", editando);
        model.addAttribute("contratos", instalacionService.contratosParaOrden());
        model.addAttribute("tecnicos", instalacionService.tecnicosParaAsignar());
        model.addAttribute("tipos", TipoOrden.values());
        model.addAttribute("estados", EstadoOrden.values());
    }
}
