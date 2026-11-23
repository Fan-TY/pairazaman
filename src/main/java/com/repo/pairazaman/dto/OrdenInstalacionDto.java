package com.repo.pairazaman.dto;

import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.model.EstadoOrden;
import com.repo.pairazaman.model.TipoOrden;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Datos del formulario de orden de servicio (instalación, reparación o mantenimiento).
 */
public class OrdenInstalacionDto {

    private Long id;
    private Long contratoId;
    private TipoOrden tipo = TipoOrden.INSTALACION;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha = LocalDate.now();

    /** Formato HH:mm (viene de un input type="time"). */
    private String hora;
    private String direccion;
    private String observaciones;
    private Long tecnicoId;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    public OrdenInstalacionDto() {
    }

    public static OrdenInstalacionDto desde(Instalacion orden) {
        OrdenInstalacionDto dto = new OrdenInstalacionDto();
        dto.id = orden.getId();
        dto.contratoId = orden.getContrato() != null ? orden.getContrato().getId() : null;
        dto.tipo = orden.getTipo() != null ? orden.getTipo() : TipoOrden.INSTALACION;
        dto.fecha = orden.getFechaInstalacion();
        dto.hora = orden.getHora() != null ? orden.getHora().format(DateTimeFormatter.ofPattern("HH:mm")) : null;
        dto.direccion = orden.getDireccion();
        dto.observaciones = orden.getObservaciones();
        dto.tecnicoId = orden.getTecnico() != null ? orden.getTecnico().getId() : null;
        dto.estado = orden.getEstadoOrden();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public TipoOrden getTipo() {
        return tipo;
    }

    public void setTipo(TipoOrden tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }
}
