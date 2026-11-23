package com.repo.pairazaman.service;

import com.repo.pairazaman.dto.TecnicoFormDto;
import com.repo.pairazaman.model.DisponibilidadTecnico;
import com.repo.pairazaman.model.Tecnico;

import java.util.List;
import java.util.Optional;

/**
 * Registro y edición del personal técnico: datos, zona y disponibilidad.
 */
public interface TecnicoService {

    List<Tecnico> listarTodos();

    Optional<Tecnico> buscarPorId(Long id);

    Tecnico guardar(TecnicoFormDto dto);

    void cambiarDisponibilidad(Long id, DisponibilidadTecnico disponibilidad);
}
