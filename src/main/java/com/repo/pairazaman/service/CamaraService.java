package com.repo.pairazaman.service;

import com.repo.pairazaman.model.Camara;

import java.util.List;
import java.util.Optional;

public interface CamaraService {

    List<Camara> listarTodas();

    Optional<Camara> buscarPorId(Long id);

    Camara guardar(Camara camara);

    void eliminar(Long id);

    long contarActivas();

    long contarPorEstado(String estado);
}
