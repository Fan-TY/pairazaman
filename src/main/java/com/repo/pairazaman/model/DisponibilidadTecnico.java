package com.repo.pairazaman.model;

/**
 * Disponibilidad del personal técnico para recibir nuevas órdenes.
 */
public enum DisponibilidadTecnico {
    DISPONIBLE("Disponible", "success"),
    OCUPADO("Ocupado", "warning"),
    NO_DISPONIBLE("No disponible", "secondary");

    private final String etiqueta;
    private final String colorBadge;

    DisponibilidadTecnico(String etiqueta, String colorBadge) {
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
