package com.repo.pairazaman.model;

/**
 * Nivel de severidad o prioridad de una incidencia.
 */
public enum Prioridad {
    BAJA("Baja", "success"),
    MEDIA("Media", "info"),
    ALTA("Alta", "warning"),
    CRITICA("Crítica", "danger");

    private final String etiqueta;
    private final String colorBadge;

    Prioridad(String etiqueta, String colorBadge) {
        this.etiqueta = etiqueta;
        this.colorBadge = colorBadge;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorBadge() {
        return colorBadge;
    }
}
