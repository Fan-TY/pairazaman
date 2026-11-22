package com.repo.pairazaman.service;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;

import java.util.List;
import java.util.Optional;

public interface PlanServicioService {

    List<PlanServicio> listar(TipoPlan tipo);

    List<PlanServicio> listarActivos();

    Optional<PlanServicio> buscarPorId(Long id);

    PlanServicio guardar(PlanServicio plan);

    PlanServicio cambiarEstado(Long id);

    long contarActivos();
}
