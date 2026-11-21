package com.repo.pairazaman.cliente.dto;

import com.repo.pairazaman.cliente.EstadoCliente;
import com.repo.pairazaman.cliente.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para registrar o actualizar un cliente.
 * El estado es opcional: si no se envía, el servicio aplica ACTIVO por defecto.
 */
public record ClienteRegistroDTO(

        @NotBlank(message = "El DNI/RUC es obligatorio")
        @Pattern(regexp = "\\d{10}|\\d{13}",
                message = "El DNI/RUC debe tener 10 dígitos (cédula) o 13 dígitos (RUC)")
        String dniRuc,

        @NotBlank(message = "Los nombres o la razón social son obligatorios")
        @Size(max = 150, message = "Los nombres o razón social no pueden superar 150 caracteres")
        String nombresRazonSocial,

        @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
        String direccion,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String correo,

        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipo,

        EstadoCliente estado
) {
}
