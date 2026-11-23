package com.repo.pairazaman.historial;

import com.repo.pairazaman.cliente.Cliente;
import com.repo.pairazaman.cliente.ClienteRepository;
import com.repo.pairazaman.cliente.EstadoCliente;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;

    public PagoService(PagoRepository pagoRepository, ClienteRepository clienteRepository) {
        this.pagoRepository = pagoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Pago> listarPagos(String estado, String filtro) {
        return pagoRepository.buscarConFiltros(estado, filtro);
    }

    @Transactional(readOnly = true)
    public List<Pago> listarMorosos() {
        return pagoRepository.listarMorosos();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByEstado(EstadoCliente.ACTIVO, Sort.by("nombresRazonSocial").ascending());
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarClientes(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarClientesActivos();
        }
        String limpio = termino.trim();
        Optional<Cliente> porDni = clienteRepository.findByDniRuc(limpio);
        if (porDni.isPresent()) {
            return List.of(porDni.get());
        }
        return clienteRepository.findByNombresRazonSocialContainingIgnoreCase(
                limpio, Sort.by("nombresRazonSocial").ascending());
    }

    @Transactional(readOnly = true)
    public Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerDeudaCliente(Long clienteId) {
        BigDecimal deuda = pagoRepository.calcularDeudaCliente(clienteId);
        return deuda != null ? deuda : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerCuotasPendientesCliente(Long clienteId) {
        return pagoRepository.findByClienteIdAndEstadoInOrderByFechaVencimientoAsc(
                clienteId, List.of("PENDIENTE", "VENCIDO"));
    }

    @Transactional(readOnly = true)
    public Pago obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante o pago no encontrado."));
    }

    @Transactional
    public Pago registrarPago(Long clienteId,
                              Long cuotaPendienteId,
                              String concepto,
                              String medioPago,
                              BigDecimal monto,
                              LocalDate fechaPago,
                              LocalDate fechaVencimiento,
                              String estado,
                              String observacion) {

        Cliente cliente = obtenerCliente(clienteId);

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        String estadoFinal = (estado == null || estado.isBlank()) ? "PAGADO" : estado.toUpperCase();

        // Si se está liquidando o abonando una cuota pendiente/vencida existente
        if (cuotaPendienteId != null) {
            Pago cuota = obtenerPagoPorId(cuotaPendienteId);
            if ("PAGADO".equals(estadoFinal)) {
                BigDecimal saldoRestante = cuota.getMonto().subtract(monto);
                if (saldoRestante.compareTo(BigDecimal.ZERO) > 0) {
                    // Abono parcial: reduce el saldo de la cuota pendiente y genera comprobante por lo pagado
                    cuota.setMonto(saldoRestante);
                    pagoRepository.save(cuota);
                } else {
                    // Pago total de la cuota: se marca como PAGADO y se emite comprobante
                    cuota.setEstado("PAGADO");
                    cuota.setMedioPago(medioPago);
                    cuota.setFechaPago(fechaPago != null ? fechaPago : LocalDate.now());
                    if (observacion != null && !observacion.isBlank()) {
                        cuota.setObservacion(observacion);
                    }
                    if (cuota.getNumeroComprobante() == null || cuota.getNumeroComprobante().isBlank()) {
                        cuota.setNumeroComprobante(generarNumeroComprobante());
                    }
                    return pagoRepository.save(cuota);
                }
            }
        }

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setConcepto(concepto != null && !concepto.isBlank() ? concepto.toUpperCase() : "MENSUALIDAD");
        pago.setMonto(monto);
        pago.setEstado(estadoFinal);
        pago.setObservacion(observacion);

        if ("PAGADO".equals(estadoFinal)) {
            pago.setMedioPago(medioPago != null && !medioPago.isBlank() ? medioPago.toUpperCase() : "EFECTIVO");
            pago.setFechaPago(fechaPago != null ? fechaPago : LocalDate.now());
            pago.setNumeroComprobante(generarNumeroComprobante());
        } else {
            pago.setMedioPago(null);
            pago.setFechaPago(null);
            pago.setFechaVencimiento(fechaVencimiento != null ? fechaVencimiento : LocalDate.now().plusDays(15));
        }

        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago marcarComoPagado(Long pagoId, String medioPago) {
        Pago pago = obtenerPagoPorId(pagoId);
        pago.setEstado("PAGADO");
        pago.setMedioPago(medioPago != null && !medioPago.isBlank() ? medioPago.toUpperCase() : "EFECTIVO");
        pago.setFechaPago(LocalDate.now());
        if (pago.getNumeroComprobante() == null || pago.getNumeroComprobante().isBlank()) {
            pago.setNumeroComprobante(generarNumeroComprobante());
        }
        return pagoRepository.save(pago);
    }

    private String generarNumeroComprobante() {
        long correlativo = pagoRepository.count() + 1;
        String fechaCorta = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        return String.format("B001-%s-%05d", fechaCorta, correlativo);
    }
}
