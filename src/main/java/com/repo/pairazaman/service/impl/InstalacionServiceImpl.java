package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.DiaAgenda;
import com.repo.pairazaman.dto.OrdenInstalacionDto;
import com.repo.pairazaman.historial.Contrato;
import com.repo.pairazaman.historial.ContratoRepository;
import com.repo.pairazaman.historial.Instalacion;
import com.repo.pairazaman.historial.InstalacionRepository;
import com.repo.pairazaman.model.DisponibilidadTecnico;
import com.repo.pairazaman.model.EstadoOrden;
import com.repo.pairazaman.model.Notificacion;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.model.TipoOrden;
import com.repo.pairazaman.repository.NotificacionRepository;
import com.repo.pairazaman.repository.TecnicoRepository;
import com.repo.pairazaman.service.InstalacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InstalacionServiceImpl implements InstalacionService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final InstalacionRepository instalacionRepository;
    private final ContratoRepository contratoRepository;
    private final TecnicoRepository tecnicoRepository;
    private final NotificacionRepository notificacionRepository;

    public InstalacionServiceImpl(InstalacionRepository instalacionRepository,
                                  ContratoRepository contratoRepository,
                                  TecnicoRepository tecnicoRepository,
                                  NotificacionRepository notificacionRepository) {
        this.instalacionRepository = instalacionRepository;
        this.contratoRepository = contratoRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instalacion> listar(EstadoOrden estado, Long tecnicoId) {
        return instalacionRepository.findAllByOrderByFechaInstalacionDescHoraDesc().stream()
                .filter(o -> estado == null || o.getEstadoOrden() == estado)
                .filter(o -> coincideTecnico(o, tecnicoId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instalacion> buscarPorId(Long id) {
        return instalacionRepository.findById(id);
    }

    @Override
    public Instalacion guardar(OrdenInstalacionDto dto) {
        if (dto.getContratoId() == null) {
            throw new IllegalArgumentException("Selecciona el contrato del cliente.");
        }
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("Selecciona el tipo de orden.");
        }
        if (dto.getFecha() == null) {
            throw new IllegalArgumentException("Indica la fecha de la orden.");
        }
        LocalTime hora = parsearHora(dto.getHora());
        boolean esNueva = dto.getId() == null;

        if (esNueva && dto.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede programar una orden en una fecha pasada.");
        }

        Contrato contrato = contratoRepository.findById(dto.getContratoId())
                .orElseThrow(() -> new IllegalArgumentException("El contrato seleccionado no existe."));

        String direccion = dto.getDireccion() != null ? dto.getDireccion().trim() : "";
        if (direccion.isEmpty() && contrato.getCliente() != null && contrato.getCliente().getDireccion() != null) {
            direccion = contrato.getCliente().getDireccion();
        }
        if (direccion.isEmpty()) {
            throw new IllegalArgumentException("Indica la dirección donde se realizará el servicio.");
        }
        if (direccion.length() > 200) {
            throw new IllegalArgumentException("La dirección no puede superar los 200 caracteres.");
        }
        if (dto.getObservaciones() != null && dto.getObservaciones().length() > 500) {
            throw new IllegalArgumentException("Las observaciones no pueden superar los 500 caracteres.");
        }

        Tecnico tecnico = null;
        if (dto.getTecnicoId() != null) {
            tecnico = tecnicoRepository.findById(dto.getTecnicoId())
                    .orElseThrow(() -> new IllegalArgumentException("El técnico seleccionado no existe."));
            validarTecnico(tecnico, dto.getFecha(), hora, dto.getId());
        }

        Instalacion orden;
        Long tecnicoAnteriorId = null;
        LocalDate fechaAnterior = null;
        LocalTime horaAnterior = null;
        if (esNueva) {
            orden = new Instalacion();
        } else {
            orden = instalacionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));
            tecnicoAnteriorId = orden.getTecnico() != null ? orden.getTecnico().getId() : null;
            fechaAnterior = orden.getFechaInstalacion();
            horaAnterior = orden.getHora();
        }

        orden.setContrato(contrato);
        orden.setCliente(contrato.getCliente());
        orden.setTipo(dto.getTipo());
        orden.setFechaInstalacion(dto.getFecha());
        orden.setHora(hora);
        orden.setDireccion(direccion);
        orden.setObservaciones(limpiar(dto.getObservaciones()));
        orden.setTecnico(tecnico);
        orden.setEstado((dto.getEstado() != null ? dto.getEstado() : EstadoOrden.PENDIENTE).name());

        Instalacion guardada = instalacionRepository.save(orden);

        if (tecnico != null) {
            boolean cambioTecnico = !tecnico.getId().equals(tecnicoAnteriorId);
            boolean reprogramada = !esNueva && !cambioTecnico
                    && (!dto.getFecha().equals(fechaAnterior) || !hora.equals(horaAnterior));
            if (cambioTecnico) {
                avisar(guardada, tecnico, "Nueva orden asignada");
            } else if (reprogramada) {
                avisar(guardada, tecnico, "Orden reprogramada");
            }
        }
        return guardada;
    }

    @Override
    public void cambiarEstado(Long id, EstadoOrden estado) {
        Instalacion orden = instalacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));
        orden.setEstado(estado.name());
        instalacionRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiaAgenda> agendaSemanal(LocalDate referencia, Long tecnicoId) {
        LocalDate lunes = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);

        List<Instalacion> ordenes = instalacionRepository
                .findByFechaInstalacionBetweenOrderByFechaInstalacionAscHoraAsc(lunes, domingo).stream()
                .filter(o -> coincideTecnico(o, tecnicoId))
                .toList();

        List<DiaAgenda> semana = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = lunes.plusDays(i);
            semana.add(new DiaAgenda(dia, ordenes.stream()
                    .filter(o -> dia.equals(o.getFechaInstalacion()))
                    .toList()));
        }
        return semana;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> contratosParaOrden() {
        return contratoRepository.findAllByOrderByCodigoAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tecnico> tecnicosParaAsignar() {
        return tecnicoRepository.findByActivoTrueOrderByNombreCompletoAsc();
    }

    // ------------------------------------------------------------------ auxiliares

    private boolean coincideTecnico(Instalacion orden, Long tecnicoId) {
        if (tecnicoId == null) {
            return true;
        }
        return orden.getTecnico() != null && tecnicoId.equals(orden.getTecnico().getId());
    }

    private LocalTime parsearHora(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Indica la hora de la orden.");
        }
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La hora no tiene un formato válido (HH:mm).");
        }
    }

    private void validarTecnico(Tecnico tecnico, LocalDate fecha, LocalTime hora, Long ordenId) {
        if (!tecnico.isActivo()) {
            throw new IllegalArgumentException("El técnico " + tecnico.getNombreCompleto() + " está inactivo.");
        }
        if (tecnico.getDisponibilidad() == DisponibilidadTecnico.NO_DISPONIBLE) {
            throw new IllegalArgumentException("El técnico " + tecnico.getNombreCompleto() + " figura como no disponible.");
        }
        boolean ocupado = instalacionRepository
                .findByTecnicoIdAndFechaInstalacionAndHora(tecnico.getId(), fecha, hora).stream()
                .anyMatch(o -> ordenId == null || !o.getId().equals(ordenId));
        if (ocupado) {
            throw new IllegalArgumentException(tecnico.getNombreCompleto() + " ya tiene una orden el "
                    + fecha.format(FORMATO_FECHA) + " a las " + hora.format(FORMATO_HORA) + ".");
        }
    }

    private void avisar(Instalacion orden, Tecnico tecnico, String titulo) {
        TipoOrden tipo = orden.getTipo() != null ? orden.getTipo() : TipoOrden.INSTALACION;
        String cliente = orden.getCliente() != null ? orden.getCliente().getNombresRazonSocial() : "cliente";
        String mensaje = titulo + ": " + tipo.getEtiqueta() + " para " + cliente
                + " el " + orden.getFechaInstalacion().format(FORMATO_FECHA)
                + " a las " + orden.getHora().format(FORMATO_HORA)
                + " en " + orden.getDireccion() + ".";
        if (mensaje.length() > 300) {
            mensaje = mensaje.substring(0, 297) + "...";
        }
        notificacionRepository.save(new Notificacion(tecnico, mensaje, orden.getId()));
    }

    private String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
