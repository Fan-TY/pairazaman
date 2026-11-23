package com.repo.pairazaman.model;

/**
 * Tipo de orden de servicio que programa la secretaria (RF-03).
 */
public enum TipoOrden {
    INSTALACION("Instalación", "success"),
    REPARACION("Reparación", "danger"),
    MANTENIMIENTO("Mantenimiento", "info");

    private final String etiqueta;
    private final String colorBadge;

    TipoOrden(String etiqueta, String colorBadge) {
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
