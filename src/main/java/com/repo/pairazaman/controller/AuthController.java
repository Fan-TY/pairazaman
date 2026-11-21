package com.repo.pairazaman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales incorrectas o usuario inactivo.");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión correctamente.");
        }
        return "auth/login";
    }
}