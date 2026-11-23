package com.repo.pairazaman.dto;

import com.repo.pairazaman.historial.Instalacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Un día de la agenda semanal con sus órdenes programadas.
 */
public class DiaAgenda {

    private static final Locale ES = Locale.forLanguageTag("es-PE");

    private final LocalDate fecha;
    private final List<Instalacion> ordenes;

    public DiaAgenda(LocalDate fecha, List<Instalacion> ordenes) {
        this.fecha = fecha;
        this.ordenes = ordenes;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public List<Instalacion> getOrdenes() {
        return ordenes;
    }

    public String getNombreDia() {
        return fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, ES);
    }

    public String getFechaCorta() {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM"));
    }

    public boolean isHoy() {
        return fecha.equals(LocalDate.now());
    }
}
