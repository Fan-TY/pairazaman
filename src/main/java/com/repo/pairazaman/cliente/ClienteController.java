package com.repo.pairazaman.cliente;

import com.repo.pairazaman.cliente.dto.CambioEstadoDTO;
import com.repo.pairazaman.cliente.dto.ClienteHistorialDTO;
import com.repo.pairazaman.cliente.dto.ClienteRegistroDTO;
import com.repo.pairazaman.cliente.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST del módulo de Gestión de Clientes (RF-01 / ECU-01).
 *
 * <pre>
 * POST   /api/clientes                 registra un cliente
 * GET    /api/clientes?nombre=&estado= lista con filtro dinámico
 * GET    /api/clientes/{id}            datos para edición
 * PUT    /api/clientes/{id}            actualiza el cliente (soporte de edición)
 * PATCH  /api/clientes/{id}/estado     cambia solo el estado
 * GET    /api/clientes/{id}/historial  contratos, pagos, reclamos e instalaciones
 * </pre>
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponseDTO registrar(@Valid @RequestBody ClienteRegistroDTO dto) {
        return clienteService.registrar(dto);
    }

    @GetMapping
    public List<ClienteResponseDTO> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoCliente estado) {
        return clienteService.listar(nombre, estado);
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO obtener(@PathVariable Long id) {
        return clienteService.obtener(id);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRegistroDTO dto) {
        return clienteService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public ClienteResponseDTO cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoDTO dto) {
        return clienteService.cambiarEstado(id, dto);
    }

    @GetMapping("/{id}/historial")
    public ClienteHistorialDTO historial(@PathVariable Long id) {
        return clienteService.historial(id);
    }
}
