package com.repo.pairazaman.model;

/**
 * Roles del sistema para autorización y control de acceso.
 */
public enum Rol {
    ROLE_ADMIN("Administrador"),
    ROLE_SUPERVISOR("Supervisor"),
    ROLE_OPERADOR("Operador");

    private final String nombreVisible;

    Rol(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
}
