package com.repo.pairazaman.model;

/**
 * Categorías o tipos de eventos detectados.
 */
public enum TipoIncidencia {
    ROBO("Robo / Hurto"),
    SOSPECHOSO("Actividad Sospechosa"),
    ACCIDENTE("Accidente / Emergencia Médica"),
    INFRAESTRUCTURA("Falla de Infraestructura"),
    MANTENIMIENTO("Mantenimiento Requerido"),
    OTRO("Otro Incidente");

    private final String etiqueta;

    TipoIncidencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
