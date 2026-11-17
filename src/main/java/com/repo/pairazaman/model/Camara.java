package com.repo.pairazaman.model;

import jakarta.persistence.*;

/**
 * Entidad de Cámara de Vigilancia para monitoreo perimetral.
 */
@Entity
@Table(name = "camaras")
public class Camara {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String ubicacion;

    @Column(length = 45)
    private String direccionIp;

    @Column(nullable = false, length = 30)
    private String estado = "OPERATIVA"; // OPERATIVA, MANTENIMIENTO, DESCONECTADA

    @Column(nullable = false)
    private boolean activa = true;

    public Camara() {
    }

    public Camara(String codigo, String ubicacion, String direccionIp, String estado) {
        this.codigo = codigo;
        this.ubicacion = ubicacion;
        this.direccionIp = direccionIp;
        this.estado = estado != null ? estado : "OPERATIVA";
        this.activa = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
