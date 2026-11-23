package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.TecnicoFormDto;
import com.repo.pairazaman.model.DisponibilidadTecnico;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.repository.TecnicoRepository;
import com.repo.pairazaman.service.TecnicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    public TecnicoServiceImpl(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAllByOrderByNombreCompletoAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tecnico> buscarPorId(Long id) {
        return tecnicoRepository.findById(id);
    }

    @Override
    public Tecnico guardar(TecnicoFormDto dto) {
        String nombre = dto.getNombreCompleto() != null ? dto.getNombreCompleto().trim() : "";
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del técnico es obligatorio.");
        }
        if (nombre.length() > 120) {
            throw new IllegalArgumentException("El nombre no puede superar los 120 caracteres.");
        }
        String zona = dto.getZona() != null ? dto.getZona().trim() : "";
        if (zona.isEmpty()) {
            throw new IllegalArgumentException("Indica la zona de trabajo del técnico.");
        }
        if (zona.length() > 100) {
            throw new IllegalArgumentException("La zona no puede superar los 100 caracteres.");
        }
        String telefono = dto.getTelefono() != null ? dto.getTelefono().trim() : "";
        if (!telefono.isEmpty() && !telefono.matches("[0-9+\\s-]{6,20}")) {
            throw new IllegalArgumentException("El teléfono solo puede tener números, espacios, + o - (6 a 20 caracteres).");
        }

        Tecnico tecnico;
        if (dto.getId() == null) {
            tecnico = new Tecnico();
        } else {
            tecnico = tecnicoRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El técnico no existe."));
        }
        tecnico.setNombreCompleto(nombre);
        tecnico.setTelefono(telefono.isEmpty() ? null : telefono);
        tecnico.setZona(zona);
        tecnico.setDisponibilidad(dto.getDisponibilidad() != null ? dto.getDisponibilidad() : DisponibilidadTecnico.DISPONIBLE);
        tecnico.setActivo(dto.isActivo());
        return tecnicoRepository.save(tecnico);
    }

    @Override
    public void cambiarDisponibilidad(Long id, DisponibilidadTecnico disponibilidad) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El técnico no existe."));
        tecnico.setDisponibilidad(disponibilidad);
        tecnicoRepository.save(tecnico);
    }
}
