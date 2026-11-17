package com.repo.pairazaman.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("codigoError", "500");
        model.addAttribute("tituloError", "Error Interno del Servidor");
        model.addAttribute("mensajeError", ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado al procesar la solicitud.");
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("codigoError", "404");
        model.addAttribute("tituloError", "Página No Encontrada");
        model.addAttribute("mensajeError", "El recurso o ruta solicitada no existe.");
        return "error";
    }
}
