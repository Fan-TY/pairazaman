package com.repo.pairazaman.model;

public enum Rol {
    ROLE_GERENTE("Gerente"),
    ROLE_SECRETARIA("Secretaria"),
    ROLE_TECNICO("Técnico"),
    ROLE_COBRANZA("Cobranza");

    private final String nombreVisible;

    Rol(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
}