package com.repo.pairazaman.model;

public enum Rol {
    ROLE_GERENTE("Gerente General"),
    ROLE_SECRETARIA("Secretaria"),
    ROLE_COBRANZA("Personal de Cobranza"),
    ROLE_TECNICO("Personal Técnico");

    private final String nombreVisible;

    Rol(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
}