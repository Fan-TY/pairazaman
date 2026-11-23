package com.repo.pairazaman.controller;

import com.repo.pairazaman.model.Notificacion;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.repository.NotificacionRepository;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final NotificacionRepository notificacionRepository;

    public DashboardController(UsuarioService usuarioService, NotificacionRepository notificacionRepository) {
        this.usuarioService = usuarioService;
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String nombre = usuarioService.buscarPorUsername(authentication.getName())
                .map(Usuario::getNombreCompleto)
                .orElse(authentication.getName());
        model.addAttribute("nombreUsuario", nombre);

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_GERENTE")) {
            return "dashboard/gerente";
        }
        if (roles.contains("ROLE_SECRETARIA")) {
            return "dashboard/secretaria";
        }
        if (roles.contains("ROLE_COBRANZA")) {
            return "dashboard/cobranza";
        }
        if (roles.contains("ROLE_TECNICO")) {
            model.addAttribute("avisos", avisosPendientes(authentication.getName()));
            return "dashboard/tecnico";
        }
        return "redirect:/acceso-denegado";
    }

    /** Marca como leídos los avisos de nuevas órdenes del técnico autenticado. */
    @PostMapping("/avisos/leidos")
    public String marcarAvisosLeidos(Authentication authentication) {
        List<Notificacion> avisos = avisosPendientes(authentication.getName());
        avisos.forEach(aviso -> aviso.setLeida(true));
        notificacionRepository.saveAll(avisos);
        return "redirect:/dashboard";
    }

    private List<Notificacion> avisosPendientes(String username) {
        return usuarioService.buscarPorUsername(username)
                .map(Usuario::getTecnico)
                .map(tecnico -> notificacionRepository.findByTecnicoIdAndLeidaFalseOrderByFechaDesc(tecnico.getId()))
                .orElse(List.of());
    }
}
