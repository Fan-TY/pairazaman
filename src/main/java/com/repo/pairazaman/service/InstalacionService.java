package com.repo.pairazaman.service;

import com.repo.pairazaman.dto.DiaAgenda;
import com.repo.pairazaman.dto.OrdenInstalacionDto;
import com.repo.pairazaman.historial.Contrato;
import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.model.EstadoOrden;
import com.repo.pairazaman.model.Tecnico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Programación de instalaciones, reparaciones y mantenimientos (RF-03 / ECU-03).
 */
public interface InstalacionService {

    /** Lista las órdenes, más recientes primero. Los filtros son opcionales (null = sin filtro). */
    List<Instalacion> listar(EstadoOrden estado, Long tecnicoId);

    Optional<Instalacion> buscarPorId(Long id);

    /** Crea o actualiza una orden. Si cambia el técnico o la fecha/hora, avisa al técnico asignado. */
    Instalacion guardar(OrdenInstalacionDto dto);

    void cambiarEstado(Long id, EstadoOrden estado);

    /** Agenda de lunes a domingo de la semana que contiene a {@code referencia}. */
    List<DiaAgenda> agendaSemanal(LocalDate referencia, Long tecnicoId);

    List<Contrato> contratosParaOrden();

    List<Tecnico> tecnicosParaAsignar();
}
