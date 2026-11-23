
package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.historial.InstalacionRepository;
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
    private final InstalacionRepository instalacionRepository;

    public IncidenciaServiceImpl(
            IncidenciaRepository incidenciaRepository,
            CamaraRepository camaraRepository,
            InstalacionRepository instalacionRepository) {

        this.incidenciaRepository = incidenciaRepository;
        this.camaraRepository = camaraRepository;
        this.instalacionRepository = instalacionRepository;
    }

    // LISTAR TODAS LAS INCIDENCIAS
    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> listarTodas() {
        return incidenciaRepository.findAllByOrderByFechaDescHoraDesc();
    }

    // BUSCAR INCIDENCIA POR ID
    @Override
    @Transactional(readOnly = true)
    public Optional<Incidencia> buscarPorId(Long id) {
        return incidenciaRepository.findById(id);
    }

    // REGISTRAR NUEVA INCIDENCIA
    @Override
    public Incidencia registrar(IncidenciaDto dto, Usuario operador) {

        validarDto(dto);

        Incidencia incidencia = new Incidencia();

        incidencia.setTitulo(dto.getTitulo().trim());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setFecha(dto.getFecha());
        incidencia.setHora(dto.getHora());

        incidencia.setTipo(dto.getTipo() != null
                ? dto.getTipo() : TipoIncidencia.OTRO);

        incidencia.setPrioridad(dto.getPrioridad() != null
                ? dto.getPrioridad() : Prioridad.MEDIA);

        incidencia.setEstado(dto.getEstado() != null
                ? dto.getEstado() : EstadoIncidencia.PENDIENTE);

        incidencia.setUbicacion(dto.getUbicacion().trim());
        incidencia.setOperador(operador);

        // VINCULAR CÁMARA
        if (dto.getCamaraId() != null) {

            Camara camara = camaraRepository.findById(dto.getCamaraId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "La cámara seleccionada no existe."));

            incidencia.setCamara(camara);
        }

        // VINCULAR ORDEN DE INSTALACIÓN
        incidencia.setInstalacion(
                buscarInstalacion(dto.getOrdenId())
        );

        return incidenciaRepository.save(incidencia);
    }

    // ACTUALIZAR INCIDENCIA
    @Override
    public Incidencia actualizar(Long id, IncidenciaDto dto) {

        validarDto(dto);

        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Incidencia no encontrada con ID: " + id));

        incidencia.setTitulo(dto.getTitulo().trim());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setFecha(dto.getFecha());
        incidencia.setHora(dto.getHora());

        incidencia.setTipo(dto.getTipo() != null
                ? dto.getTipo() : TipoIncidencia.OTRO);

        incidencia.setPrioridad(dto.getPrioridad() != null
                ? dto.getPrioridad() : Prioridad.MEDIA);

        incidencia.setEstado(dto.getEstado() != null
                ? dto.getEstado() : EstadoIncidencia.PENDIENTE);

        incidencia.setUbicacion(dto.getUbicacion().trim());

        // ACTUALIZAR CÁMARA
        if (dto.getCamaraId() != null) {

            Camara camara = camaraRepository.findById(dto.getCamaraId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "La cámara seleccionada no existe."));

            incidencia.setCamara(camara);

        } else {
            incidencia.setCamara(null);
        }

        // ACTUALIZAR ORDEN DE INSTALACIÓN
        // Si ordenId es null, se elimina la relación anterior.
        incidencia.setInstalacion(
                buscarInstalacion(dto.getOrdenId())
        );

        return incidenciaRepository.save(incidencia);
    }

    // CAMBIAR ESTADO
    @Override
    public void cambiarEstado(Long id, EstadoIncidencia nuevoEstado) {

        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Incidencia no encontrada con ID: " + id));

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio.");
        }

        incidencia.setEstado(nuevoEstado);

        incidenciaRepository.save(incidencia);
    }

    // ELIMINAR INCIDENCIA
    @Override
    public void eliminar(Long id) {
        incidenciaRepository.deleteById(id);
    }

    // CONTAR INCIDENCIAS POR ESTADO
    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoIncidencia estado) {
        return incidenciaRepository.countByEstado(estado);
    }

    // CONTAR TOTAL DE INCIDENCIAS
    @Override
    @Transactional(readOnly = true)
    public long contarTotal() {
        return incidenciaRepository.count();
    }

    // LISTAR INCIDENCIAS RECIENTES
    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> listarRecientes() {
        return incidenciaRepository.findTop5ByOrderByFechaRegistroDesc();
    }

    // FILTRAR INCIDENCIAS
    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> filtrar(
            EstadoIncidencia estado,
            TipoIncidencia tipo) {

        return incidenciaRepository.filtrar(estado, tipo);
    }

    // BUSCAR ORDEN DE INSTALACIÓN POR ID
    private Instalacion buscarInstalacion(Long ordenId) {

        if (ordenId == null) {
            return null;
        }

        return instalacionRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La orden de instalación no existe con ID: "
                                + ordenId));
    }

    // VALIDACIONES
    private void validarDto(IncidenciaDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos de la incidencia son obligatorios.");
        }

        if (dto.getTitulo() == null
                || dto.getTitulo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El título de la incidencia es obligatorio.");
        }

        if (dto.getUbicacion() == null
                || dto.getUbicacion().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La ubicación de la incidencia es obligatoria.");
        }

        if (dto.getFecha() == null) {

            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }
    }
}
