package com.repo.pairazaman.cliente.dto;

import java.util.List;

/**
 * Historial consolidado de un cliente (RF-01 / ECU-01).
 * Se construye dentro de una transacción de solo lectura para inicializar
 * las colecciones LAZY antes de salir de la capa de persistencia.
 */
public record ClienteHistorialDTO(
        Long id,
        String dniRuc,
        String nombresRazonSocial,
        List<ContratoDTO> contratos,
        List<PagoDTO> pagos,
        List<ReclamoDTO> reclamos,
        List<InstalacionDTO> instalaciones
) {
}
