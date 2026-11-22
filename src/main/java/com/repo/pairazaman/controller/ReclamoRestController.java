
package com.repo.pairazaman.controller;

import com.repo.pairazaman.cliente.Cliente;
import com.repo.pairazaman.cliente.ClienteRepository;
import com.repo.pairazaman.cliente.dto.ReclamoDTO;
import com.repo.pairazaman.historial.Reclamo;
import com.repo.pairazaman.historial.ReclamoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reclamos")
@CrossOrigin(origins = "http://localhost:5173")
public class ReclamoRestController {

    private final ReclamoRepository reclamoRepository;
    private final ClienteRepository clienteRepository;

    public ReclamoRestController(
            ReclamoRepository reclamoRepository,
            ClienteRepository clienteRepository) {
        this.reclamoRepository = reclamoRepository;
        this.clienteRepository = clienteRepository;
    }

    // Listar reclamos
    @GetMapping
    public List<ReclamoDTO> listar() {
        return reclamoRepository.findAll()
                .stream()
                .map(ReclamoDTO::from)
                .toList();
    }

    // Registrar reclamo
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearReclamoRequest request) {

        if (request.clienteId() == null || request.descripcion() == null
                || request.descripcion().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Completa el cliente y la descripción"));
        }

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElse(null);

        if (cliente == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El cliente indicado no existe"));
        }

        Reclamo reclamo = new Reclamo();
        reclamo.setCliente(cliente);
        reclamo.setDescripcion(request.descripcion().trim());
        reclamo.setFechaRegistro(LocalDateTime.now());
        reclamo.setEstado("PENDIENTE");

        Reclamo guardado = reclamoRepository.save(reclamo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReclamoDTO.from(guardado));
    }

    // Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        Reclamo reclamo = reclamoRepository.findById(id).orElse(null);

        if (reclamo == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.estado() == null || request.estado().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El estado es obligatorio"));
        }

        reclamo.setEstado(request.estado());
        Reclamo actualizado = reclamoRepository.save(reclamo);

        return ResponseEntity.ok(ReclamoDTO.from(actualizado));
    }

    // Eliminar reclamo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!reclamoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reclamoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CrearReclamoRequest(
            Long clienteId,
            String descripcion
    ) {}

    public record CambiarEstadoRequest(
            String estado
    ) {}
}
