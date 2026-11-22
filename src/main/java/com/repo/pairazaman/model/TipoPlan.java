package com.repo.pairazaman.model;

public enum TipoPlan {
    INTERNET("Internet"),
    TV("Televisión por cable"),
    COMBO("Combo Internet + TV");

    private final String nombreVisible;

    TipoPlan(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public boolean tieneInternet() {
        return this == INTERNET || this == COMBO;
    }

    public boolean tieneTv() {
        return this == TV || this == COMBO;
    }
}
