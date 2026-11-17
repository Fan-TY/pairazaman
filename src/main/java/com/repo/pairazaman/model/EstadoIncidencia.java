package com.repo.pairazaman.model;

/**
 * Estados del ciclo de vida de una incidencia.
 */
public enum EstadoIncidencia {
    PENDIENTE("Pendiente", "warning"),
    EN_PROCESO("En Proceso", "primary"),
    RESUELTO("Resuelto", "success"),
    CERRADO("Cerrado", "secondary");

    private final String etiqueta;
    private final String colorBadge;

    EstadoIncidencia(String etiqueta, String colorBadge) {
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
