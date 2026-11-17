package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.model.Camara;
import com.repo.pairazaman.repository.CamaraRepository;
import com.repo.pairazaman.service.CamaraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CamaraServiceImpl implements CamaraService {

    private final CamaraRepository camaraRepository;

    public CamaraServiceImpl(CamaraRepository camaraRepository) {
        this.camaraRepository = camaraRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Camara> listarTodas() {
        return camaraRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Camara> buscarPorId(Long id) {
        return camaraRepository.findById(id);
    }

    @Override
    public Camara guardar(Camara camara) {
        if (camara.getCodigo() == null || camara.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de cámara es obligatorio.");
        }
        return camaraRepository.save(camara);
    }

    @Override
    public void eliminar(Long id) {
        camaraRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivas() {
        return camaraRepository.countByActivaTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        return camaraRepository.countByEstado(estado);
    }
}
