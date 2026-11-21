package com.repo.pairazaman.cliente;

import com.repo.pairazaman.cliente.dto.CambioEstadoDTO;
import com.repo.pairazaman.cliente.dto.ClienteHistorialDTO;
import com.repo.pairazaman.cliente.dto.ClienteRegistroDTO;
import com.repo.pairazaman.cliente.dto.ClienteResponseDTO;
import com.repo.pairazaman.cliente.dto.ContratoDTO;
import com.repo.pairazaman.cliente.dto.InstalacionDTO;
import com.repo.pairazaman.cliente.dto.PagoDTO;
import com.repo.pairazaman.cliente.dto.ReclamoDTO;
import com.repo.pairazaman.cliente.exception.RecursoDuplicadoException;
import com.repo.pairazaman.cliente.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio del módulo de Gestión de Clientes (RF-01 / ECU-01).
 * Actúa como capa DAO/servicio entre el controlador y el repositorio.
 */
@Service
@Transactional
public class ClienteService {

    private static final Sort ORDEN_NOMBRE = Sort.by(Sort.Direction.ASC, "nombresRazonSocial");

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDTO registrar(ClienteRegistroDTO dto) {
        String dniRuc = dto.dniRuc().trim();
        if (clienteRepository.existsByDniRuc(dniRuc)) {
            throw new RecursoDuplicadoException("Ya existe un cliente con el DNI/RUC " + dniRuc);
        }

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, dto);
        cliente.setEstado(dto.estado() != null ? dto.estado() : EstadoCliente.ACTIVO);

        return ClienteResponseDTO.from(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO actualizar(Long id, ClienteRegistroDTO dto) {
        Cliente cliente = buscar(id);
        String dniRuc = dto.dniRuc().trim();

        if (!cliente.getDniRuc().equals(dniRuc) && clienteRepository.existsByDniRuc(dniRuc)) {
            throw new RecursoDuplicadoException("Ya existe un cliente con el DNI/RUC " + dniRuc);
        }

        aplicarDatos(cliente, dto);
        if (dto.estado() != null) {
            cliente.setEstado(dto.estado());
        }

        return ClienteResponseDTO.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listar(String nombre, EstadoCliente estado) {
        String filtroNombre = (nombre == null || nombre.isBlank()) ? null : nombre.trim();

        List<Cliente> clientes;
        if (filtroNombre != null && estado != null) {
            clientes = clienteRepository
                    .findByNombresRazonSocialContainingIgnoreCaseAndEstado(filtroNombre, estado, ORDEN_NOMBRE);
        } else if (filtroNombre != null) {
            clientes = clienteRepository
                    .findByNombresRazonSocialContainingIgnoreCase(filtroNombre, ORDEN_NOMBRE);
        } else if (estado != null) {
            clientes = clienteRepository.findByEstado(estado, ORDEN_NOMBRE);
        } else {
            clientes = clienteRepository.findAll(ORDEN_NOMBRE);
        }

        return clientes.stream().map(ClienteResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtener(Long id) {
        return ClienteResponseDTO.from(buscar(id));
    }

    public ClienteResponseDTO cambiarEstado(Long id, CambioEstadoDTO dto) {
        Cliente cliente = buscar(id);
        cliente.setEstado(dto.estado());
        return ClienteResponseDTO.from(clienteRepository.save(cliente));
    }

    /**
     * Devuelve el historial completo. Las colecciones LAZY se inicializan dentro
     * de esta transacción de solo lectura y se transforman a DTOs antes de salir.
     */
    @Transactional(readOnly = true)
    public ClienteHistorialDTO historial(Long id) {
        Cliente cliente = buscar(id);

        return new ClienteHistorialDTO(
                cliente.getId(),
                cliente.getDniRuc(),
                cliente.getNombresRazonSocial(),
                cliente.getContratos().stream().map(ContratoDTO::from).toList(),
                cliente.getPagos().stream().map(PagoDTO::from).toList(),
                cliente.getReclamos().stream().map(ReclamoDTO::from).toList(),
                cliente.getInstalaciones().stream().map(InstalacionDTO::from).toList()
        );
    }

    private Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    private void aplicarDatos(Cliente cliente, ClienteRegistroDTO dto) {
        cliente.setDniRuc(dto.dniRuc().trim());
        cliente.setNombresRazonSocial(dto.nombresRazonSocial().trim());
        cliente.setDireccion(limpiar(dto.direccion()));
        cliente.setTelefono(limpiar(dto.telefono()));
        cliente.setCorreo(limpiar(dto.correo()));
        cliente.setTipo(dto.tipo());
    }

    private String limpiar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
