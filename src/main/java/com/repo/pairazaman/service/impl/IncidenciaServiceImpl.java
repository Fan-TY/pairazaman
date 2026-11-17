package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.*;
import com.repo.pairazaman.repository.CamaraRepository;
import com.repo.pairazaman.repository.IncidenciaRepository;
import com.repo.pairazaman.service.IncidenciaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final CamaraRepository camaraRepository;

    public IncidenciaServiceImpl(IncidenciaRepository incidenciaRepository,
                                 CamaraRepository camaraRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.camaraRepository = camaraRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> listarTodas() {
        return incidenciaRepository.findAllByOrderByFechaDescHoraDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Incidencia> buscarPorId(Long id) {
        return incidenciaRepository.findById(id);
    }

    @Override
    public Incidencia registrar(IncidenciaDto dto, Usuario operador) {
        validarDto(dto);

        Incidencia incidencia = new Incidencia();
        incidencia.setTitulo(dto.getTitulo().trim());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setFecha(dto.getFecha());
        incidencia.setHora(dto.getHora());
        incidencia.setTipo(dto.getTipo() != null ? dto.getTipo() : TipoIncidencia.OTRO);
        incidencia.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : Prioridad.MEDIA);
        incidencia.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoIncidencia.PENDIENTE);
        incidencia.setUbicacion(dto.getUbicacion().trim());
        incidencia.setOperador(operador);

        if (dto.getCamaraId() != null) {
            camaraRepository.findById(dto.getCamaraId()).ifPresent(incidencia::setCamara);
        }

        return incidenciaRepository.save(incidencia);
    }

    @Override
    public Incidencia actualizar(Long id, IncidenciaDto dto) {
        validarDto(dto);

        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada con ID: " + id));

        incidencia.setTitulo(dto.getTitulo().trim());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setFecha(dto.getFecha());
        incidencia.setHora(dto.getHora());
        incidencia.setTipo(dto.getTipo() != null ? dto.getTipo() : TipoIncidencia.OTRO);
        incidencia.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : Prioridad.MEDIA);
        incidencia.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoIncidencia.PENDIENTE);
        incidencia.setUbicacion(dto.getUbicacion().trim());

        if (dto.getCamaraId() != null) {
            Camara camara = camaraRepository.findById(dto.getCamaraId()).orElse(null);
            incidencia.setCamara(camara);
        } else {
            incidencia.setCamara(null);
        }

        return incidenciaRepository.save(incidencia);
    }

    @Override
    public void cambiarEstado(Long id, EstadoIncidencia nuevoEstado) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada con ID: " + id));
        incidencia.setEstado(nuevoEstado);
        incidenciaRepository.save(incidencia);
    }

    @Override
    public void eliminar(Long id) {
        incidenciaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoIncidencia estado) {
        return incidenciaRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTotal() {
        return incidenciaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> listarRecientes() {
        return incidenciaRepository.findTop5ByOrderByFechaRegistroDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> filtrar(EstadoIncidencia estado, TipoIncidencia tipo) {
        return incidenciaRepository.filtrar(estado, tipo);
    }

    private void validarDto(IncidenciaDto dto) {
        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la incidencia es obligatorio.");
        }
        if (dto.getUbicacion() == null || dto.getUbicacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación de la incidencia es obligatoria.");
        }
        if (dto.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
    }
}
