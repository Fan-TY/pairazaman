package com.repo.pairazaman.model;

/**
 * Estados de una orden de instalación / reparación / mantenimiento.
 * La columna {@code estado} de {@code Instalacion} sigue siendo un String;
 * aquí se guarda el {@link #name()} de cada valor.
 */
public enum EstadoOrden {
    PENDIENTE("Pendiente", "warning"),
    EN_PROCESO("En proceso", "primary"),
    COMPLETADO("Completado", "success");

    private final String etiqueta;
    private final String colorBadge;

    EstadoOrden(String etiqueta, String colorBadge) {
        this.etiqueta = etiqueta;
        this.colorBadge = colorBadge;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorBadge() {
        return colorBadge;
    }

    /** Convierte el texto guardado en BD; ante un valor nulo o desconocido devuelve PENDIENTE. */
    public static EstadoOrden desde(String valor) {
        if (valor != null) {
            String limpio = valor.trim();
            for (EstadoOrden estado : values()) {
                if (estado.name().equalsIgnoreCase(limpio) || estado.etiqueta.equalsIgnoreCase(limpio)) {
                    return estado;
                }
            }
        }
        return PENDIENTE;
    }
}
