package com.repo.pairazaman.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Aviso interno para un técnico (por ejemplo, cuando se le asigna o reprograma una orden).
 * Se muestra en el panel del técnico hasta que lo marca como leído.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @Column(nullable = false, length = 300)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean leida = false;

    @Column(name = "instalacion_id")
    private Long instalacionId;

    public Notificacion() {
    }

    public Notificacion(Tecnico tecnico, String mensaje, Long instalacionId) {
        this.tecnico = tecnico;
        this.mensaje = mensaje;
        this.instalacionId = instalacionId;
    }

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public Long getInstalacionId() {
        return instalacionId;
    }

    public void setInstalacionId(Long instalacionId) {
        this.instalacionId = instalacionId;
    }
}
