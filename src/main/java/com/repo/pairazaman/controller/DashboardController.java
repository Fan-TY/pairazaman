package com.repo.pairazaman.controller;

import com.repo.pairazaman.model.EstadoIncidencia;
import com.repo.pairazaman.service.CamaraService;
import com.repo.pairazaman.service.IncidenciaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final IncidenciaService incidenciaService;
    private final CamaraService camaraService;

    public DashboardController(IncidenciaService incidenciaService, CamaraService camaraService) {
        this.incidenciaService = incidenciaService;
        this.camaraService = camaraService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Métricas de Incidencias
        long totalIncidencias = incidenciaService.contarTotal();
        long pendientes = incidenciaService.contarPorEstado(EstadoIncidencia.PENDIENTE);
        long enProceso = incidenciaService.contarPorEstado(EstadoIncidencia.EN_PROCESO);
        long resueltas = incidenciaService.contarPorEstado(EstadoIncidencia.RESUELTO);

        // Métricas de Cámaras
        long camarasActivas = camaraService.contarActivas();
        long totalCamaras = camaraService.listarTodas().size();

        model.addAttribute("totalIncidencias", totalIncidencias);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("resueltas", resueltas);
        model.addAttribute("camarasActivas", camarasActivas);
        model.addAttribute("totalCamaras", totalCamaras);

        // Últimas incidencias
        model.addAttribute("incidenciasRecientes", incidenciaService.listarRecientes());
        model.addAttribute("usuarioActual", userDetails != null ? userDetails.getUsername() : "Invitado");

        return "dashboard";
    }
}
