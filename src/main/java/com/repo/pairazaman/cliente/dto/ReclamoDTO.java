package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.historial.Reclamo;

import java.time.LocalDateTime;

public record ReclamoDTO(
        Long id,
        String descripcion,
        LocalDateTime fechaRegistro,
        String estado
) {

    public static ReclamoDTO from(Reclamo reclamo) {
        return new ReclamoDTO(
                reclamo.getId(),
                reclamo.getDescripcion(),
                reclamo.getFechaRegistro(),
                reclamo.getEstado()
        );
    }
}
