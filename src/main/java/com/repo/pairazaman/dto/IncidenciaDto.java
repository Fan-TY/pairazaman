package com.repo.pairazaman.dto;

import com.repo.pairazaman.model.EstadoIncidencia;
import com.repo.pairazaman.model.Prioridad;
import com.repo.pairazaman.model.TipoIncidencia;

import java.time.LocalDate;

/**
 * Objeto de transferencia de datos para la creación y edición de incidencias desde la vista.
 */
public class IncidenciaDto {

    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;
    private String hora;
    private TipoIncidencia tipo;
    private Prioridad prioridad;
    private EstadoIncidencia estado;
    private String ubicacion;
    private Long camaraId;

    public IncidenciaDto() {
        this.fecha = LocalDate.now();
        this.prioridad = Prioridad.MEDIA;
        this.estado = EstadoIncidencia.PENDIENTE;
        this.tipo = TipoIncidencia.OTRO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public TipoIncidencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidencia tipo) {
        this.tipo = tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getCamaraId() {
        return camaraId;
    }

    public void setCamaraId(Long camaraId) {
        this.camaraId = camaraId;
    }
}
