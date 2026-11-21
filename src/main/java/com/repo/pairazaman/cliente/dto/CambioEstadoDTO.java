package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.cliente.EstadoCliente;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo del PATCH /api/clientes/{id}/estado.
 */
public record CambioEstadoDTO(
        @NotNull(message = "El estado es obligatorio")
        EstadoCliente estado
) {
}
