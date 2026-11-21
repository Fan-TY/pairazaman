package com.repo.pairazaman.cliente;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.repo.pairazaman.historial.Contrato;
import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.historial.Pago;
import com.repo.pairazaman.historial.Reclamo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente del negocio. Entidad raíz del módulo de Gestión de Clientes (RF-01 / ECU-01).
 *
 * <p>Las colecciones de historial se cargan de forma perezosa ({@link FetchType#LAZY}) y se
 * exponen al exterior únicamente mediante DTOs. Aun así se marcan con {@link JsonIgnore}
 * como segunda barrera contra la recursión infinita.</p>
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_ruc", nullable = false, unique = true, length = 13)
    private String dniRuc;

    @Column(name = "nombres_razon_social", nullable = false, length = 150)
    private String nombresRazonSocial;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCliente tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Contrato> contratos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reclamo> reclamos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Instalacion> instalaciones = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoCliente.ACTIVO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDniRuc() {
        return dniRuc;
    }

    public void setDniRuc(String dniRuc) {
        this.dniRuc = dniRuc;
    }

    public String getNombresRazonSocial() {
        return nombresRazonSocial;
    }

    public void setNombresRazonSocial(String nombresRazonSocial) {
        this.nombresRazonSocial = nombresRazonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<Reclamo> getReclamos() {
        return reclamos;
    }

    public void setReclamos(List<Reclamo> reclamos) {
        this.reclamos = reclamos;
    }

    public List<Instalacion> getInstalaciones() {
        return instalaciones;
    }

    public void setInstalaciones(List<Instalacion> instalaciones) {
        this.instalaciones = instalaciones;
    }
}
