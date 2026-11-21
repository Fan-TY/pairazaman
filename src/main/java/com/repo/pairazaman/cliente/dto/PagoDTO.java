package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.historial.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoDTO(
        Long id,
        BigDecimal monto,
        LocalDate fechaPago,
        String estado
) {

    public static PagoDTO from(Pago pago) {
        return new PagoDTO(
                pago.getId(),
                pago.getMonto(),
                pago.getFechaPago(),
                pago.getEstado()
        );
    }
}
