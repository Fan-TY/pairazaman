
package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.*;
import com.repo.pairazaman.service.CamaraService;
import com.repo.pairazaman.service.IncidenciaService;
import com.repo.pairazaman.service.InstalacionService;
import com.repo.pairazaman.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
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
import java.util.Set;

@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;
    private final CamaraService camaraService;
    private final UsuarioService usuarioService;
    private final InstalacionService instalacionService;

    public IncidenciaController(
            IncidenciaService incidenciaService,
            CamaraService camaraService,
            UsuarioService usuarioService,
            InstalacionService instalacionService) {

        this.incidenciaService = incidenciaService;
        this.camaraService = camaraService;
        this.usuarioService = usuarioService;
        this.instalacionService = instalacionService;
    }

    // ==========================================
    // LISTAR INCIDENCIAS
    // ==========================================

    @GetMapping
    public String listar(
            @RequestParam(required = false) EstadoIncidencia estado,
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

    // ==========================================
    // NUEVA INCIDENCIA
    // ==========================================

    @GetMapping("/nueva")
    public String nuevaIncidencia(Model model) {

        IncidenciaDto dto = new IncidenciaDto();

        dto.setFecha(LocalDate.now());

        dto.setHora(
                LocalTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm")
                )
        );

        cargarAtributosFormulario(
                model,
                dto,
                "Registrar Nueva Incidencia",
                "/incidencias/guardar"
        );

        return "incidencias/form";
    }

    // ==========================================
    // GUARDAR O ACTUALIZAR INCIDENCIA
    // ==========================================

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("incidenciaDto") IncidenciaDto dto,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            Usuario operador = null;

            if (userDetails != null) {
                operador = usuarioService
                        .buscarPorUsername(userDetails.getUsername())
                        .orElse(null);
            }

            if (dto.getId() == null) {

                incidenciaService.registrar(dto, operador);

                redirectAttributes.addFlashAttribute(
                        "mensajeExito",
                        "Incidencia registrada correctamente."
                );

            } else {

                incidenciaService.actualizar(dto.getId(), dto);

                redirectAttributes.addFlashAttribute(
                        "mensajeExito",
                        "Incidencia actualizada correctamente."
                );
            }

            return "redirect:/incidencias";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("mensajeError", ex.getMessage());

            cargarAtributosFormulario(
                    model,
                    dto,
                    dto.getId() == null
                            ? "Registrar Nueva Incidencia"
                            : "Editar Incidencia",
                    "/incidencias/guardar"
            );

            return "incidencias/form";
        }
    }

    // ==========================================
    // EDITAR INCIDENCIA
    // ==========================================

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

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

                    // Recuperar cámara asociada
                    if (incidencia.getCamara() != null) {
                        dto.setCamaraId(
                                incidencia.getCamara().getId()
                        );
                    }

                    // Recuperar orden de instalación asociada
                    if (incidencia.getInstalacion() != null) {
                        dto.setOrdenId(
                                incidencia.getInstalacion().getId()
                        );
                    }

                    cargarAtributosFormulario(
                            model,
                            dto,
                            "Editar Incidencia #" + id,
                            "/incidencias/guardar"
                    );

                    return "incidencias/form";
                })

                .orElseGet(() -> {

                    redirectAttributes.addFlashAttribute(
                            "mensajeError",
                            "Incidencia no encontrada."
                    );

                    return "redirect:/incidencias";
                });
    }

    // ==========================================
    // DETALLE DE INCIDENCIA
    // ==========================================

    @GetMapping("/detalle/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return incidenciaService.buscarPorId(id)

                .map(incidencia -> {

                    model.addAttribute("incidencia", incidencia);

                    model.addAttribute(
                            "estados",
                            EstadoIncidencia.values()
                    );

                    return "incidencias/detalle";
                })

                .orElseGet(() -> {

                    redirectAttributes.addFlashAttribute(
                            "mensajeError",
                            "Incidencia no encontrada."
                    );

                    return "redirect:/incidencias";
                });
    }

    // ==========================================
    // CAMBIAR ESTADO
    // ==========================================

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoIncidencia estado,
            RedirectAttributes redirectAttributes) {

        try {

            incidenciaService.cambiarEstado(id, estado);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Estado actualizado a: " + estado.getEtiqueta()
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "Error al actualizar estado."
            );
        }

        return "redirect:/incidencias/detalle/" + id;
    }

    // ==========================================
    // ELIMINAR INCIDENCIA
    // SOLO GERENTE Y SECRETARIA
    // ==========================================

    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Verificar permisos del usuario
        Set<String> roles = AuthorityUtils.authorityListToSet(
                authentication.getAuthorities()
        );

        boolean tienePermiso =
                roles.contains("ROLE_GERENTE")
                        || roles.contains("ROLE_SECRETARIA");

        if (!tienePermiso) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No tienes permisos para eliminar incidencias."
            );

            return "redirect:/incidencias";
        }

        try {

            incidenciaService.eliminar(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Incidencia eliminada con éxito."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No se pudo eliminar la incidencia."
            );
        }

        return "redirect:/incidencias";
    }

    // ==========================================
    // CARGAR ATRIBUTOS DE LOS FORMULARIOS
    // ==========================================

    private void cargarAtributosFormulario(
            Model model,
            IncidenciaDto dto,
            String tituloVista,
            String actionUrl) {

        model.addAttribute("incidenciaDto", dto);
        model.addAttribute("tituloVista", tituloVista);
        model.addAttribute("actionUrl", actionUrl);

        model.addAttribute("tipos", TipoIncidencia.values());
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("estados", EstadoIncidencia.values());

        // Cargar cámaras disponibles
        model.addAttribute(
                "camaras",
                camaraService.listarTodas()
        );

        // Cargar órdenes de instalación disponibles
        model.addAttribute(
                "ordenesInstalacion",
                instalacionService.listar(null, null)
        );
    }
}
