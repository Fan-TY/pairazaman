package com.repo.pairazaman.service;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.EstadoIncidencia;
import com.repo.pairazaman.model.Incidencia;
import com.repo.pairazaman.model.TipoIncidencia;
import com.repo.pairazaman.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IncidenciaService {

    List<Incidencia> listarTodas();

    Optional<Incidencia> buscarPorId(Long id);

    Incidencia registrar(IncidenciaDto dto, Usuario operador);

    Incidencia actualizar(Long id, IncidenciaDto dto);

    Incidencia cambiarEstado(Long id, EstadoIncidencia nuevoEstado);

    void eliminar(Long id);

    long contarPorEstado(EstadoIncidencia estado);

    long contarTotal();

    List<Incidencia> listarRecientes();

    List<Incidencia> filtrar(EstadoIncidencia estado, TipoIncidencia tipo);
}
