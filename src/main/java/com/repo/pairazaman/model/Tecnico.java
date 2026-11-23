package com.repo.pairazaman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tecnicos")
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DisponibilidadTecnico disponibilidad = DisponibilidadTecnico.DISPONIBLE;

    @Column(nullable = false)
    private boolean activo = true;

    public Tecnico() {
    }

    public Tecnico(String nombreCompleto, String telefono) {
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.activo = true;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    /** Los técnicos creados antes de este campo no tienen valor en BD: se consideran disponibles. */
    public DisponibilidadTecnico getDisponibilidad() {
        return disponibilidad != null ? disponibilidad : DisponibilidadTecnico.DISPONIBLE;
    }

    public void setDisponibilidad(DisponibilidadTecnico disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
