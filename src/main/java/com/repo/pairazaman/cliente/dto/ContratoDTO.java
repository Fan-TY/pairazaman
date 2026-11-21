package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.historial.Contrato;

import java.time.LocalDate;

public record ContratoDTO(
        Long id,
        String codigo,
        LocalDate fechaInicio,
        String estado
) {

    public static ContratoDTO from(Contrato contrato) {
        return new ContratoDTO(
                contrato.getId(),
                contrato.getCodigo(),
                contrato.getFechaInicio(),
                contrato.getEstado()
        );
    }
}
