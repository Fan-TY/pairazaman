package com.repo.pairazaman.controller;

import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;

    public DashboardController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
            return "dashboard/tecnico";
        }
        return "redirect:/acceso-denegado";
    }
}