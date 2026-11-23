package com.repo.pairazaman.dto;

import com.repo.pairazaman.model.DisponibilidadTecnico;
import com.repo.pairazaman.model.Tecnico;

/**
 * Datos del formulario de registro / edición del personal técnico.
 */
public class TecnicoFormDto {

    private Long id;
    private String nombreCompleto;
    private String telefono;
    private String zona;
    private DisponibilidadTecnico disponibilidad = DisponibilidadTecnico.DISPONIBLE;
    private boolean activo = true;

    public TecnicoFormDto() {
    }

    public static TecnicoFormDto desde(Tecnico tecnico) {
        TecnicoFormDto dto = new TecnicoFormDto();
        dto.id = tecnico.getId();
        dto.nombreCompleto = tecnico.getNombreCompleto();
        dto.telefono = tecnico.getTelefono();
        dto.zona = tecnico.getZona();
        dto.disponibilidad = tecnico.getDisponibilidad();
        dto.activo = tecnico.isActivo();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public DisponibilidadTecnico getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(DisponibilidadTecnico disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
