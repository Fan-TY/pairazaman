package com.repo.pairazaman.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_servicio")
public class PlanServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPlan tipo;

    private Integer velocidadMbps;

    private Integer cantidadCanales;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoInstalacion = BigDecimal.ZERO;

    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public PlanServicio() {
    }

    public PlanServicio(String nombre, TipoPlan tipo, Integer velocidadMbps, Integer cantidadCanales,
                        BigDecimal precioMensual, BigDecimal costoInstalacion, String descripcion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.velocidadMbps = velocidadMbps;
        this.cantidadCanales = cantidadCanales;
        this.precioMensual = precioMensual;
        this.costoInstalacion = costoInstalacion;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoPlan getTipo() {
        return tipo;
    }

    public void setTipo(TipoPlan tipo) {
        this.tipo = tipo;
    }

    public Integer getVelocidadMbps() {
        return velocidadMbps;
    }

    public void setVelocidadMbps(Integer velocidadMbps) {
        this.velocidadMbps = velocidadMbps;
    }

    public Integer getCantidadCanales() {
        return cantidadCanales;
    }

    public void setCantidadCanales(Integer cantidadCanales) {
        this.cantidadCanales = cantidadCanales;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(BigDecimal precioMensual) {
        this.precioMensual = precioMensual;
    }

    public BigDecimal getCostoInstalacion() {
        return costoInstalacion;
    }

    public void setCostoInstalacion(BigDecimal costoInstalacion) {
        this.costoInstalacion = costoInstalacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
