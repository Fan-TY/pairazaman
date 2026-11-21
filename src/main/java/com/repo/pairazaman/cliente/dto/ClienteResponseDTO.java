package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.cliente.Cliente;
import com.repo.pairazaman.cliente.EstadoCliente;
import com.repo.pairazaman.cliente.TipoCliente;

import java.time.LocalDateTime;

/**
 * Representación de salida de un cliente. Evita serializar directamente la entidad
 * y sus colecciones perezosas.
 */
public record ClienteResponseDTO(
        Long id,
        String dniRuc,
        String nombresRazonSocial,
        String direccion,
        String telefono,
        String correo,
        TipoCliente tipo,
        EstadoCliente estado,
        LocalDateTime fechaRegistro
) {

    public static ClienteResponseDTO from(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getDniRuc(),
                cliente.getNombresRazonSocial(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getTipo(),
                cliente.getEstado(),
                cliente.getFechaRegistro()
        );
    }
}
