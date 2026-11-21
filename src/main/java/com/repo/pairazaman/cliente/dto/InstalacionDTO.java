package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.historial.Instalacion;

import java.time.LocalDate;

public record InstalacionDTO(
        Long id,
        LocalDate fechaInstalacion,
        String direccion,
        String estado
) {

    public static InstalacionDTO from(Instalacion instalacion) {
        return new InstalacionDTO(
                instalacion.getId(),
                instalacion.getFechaInstalacion(),
                instalacion.getDireccion(),
                instalacion.getEstado()
        );
    }
}
