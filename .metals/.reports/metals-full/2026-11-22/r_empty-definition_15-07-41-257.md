error id: file:///D:/pairazaman/src/main/java/com/repo/pairazaman/controller/IncidenciaRestController.java:_empty_/ResponseEntity#badRequest#body#
file:///D:/pairazaman/src/main/java/com/repo/pairazaman/controller/IncidenciaRestController.java
empty definition using pc, found symbol in pc: _empty_/ResponseEntity#badRequest#body#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1610
uri: file:///D:/pairazaman/src/main/java/com/repo/pairazaman/controller/IncidenciaRestController.java
text:
```scala

package com.repo.pairazaman.controller;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.EstadoIncidencia;
import com.repo.pairazaman.model.Incidencia;
import com.repo.pairazaman.service.IncidenciaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
@CrossOrigin(origins = "http://localhost:5173")
public class IncidenciaRestController {

    private final IncidenciaService incidenciaService;

    public IncidenciaRestController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @GetMapping
    public ResponseEntity<List<Incidencia>> listar() {
        return ResponseEntity.ok(incidenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return incidenciaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody IncidenciaDto dto) {
        try {
            Incidencia incidencia = incidenciaService.registrar(dto, null);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(incidencia);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .bo@@dy(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody IncidenciaDto dto) {
        try {
            Incidencia incidencia = incidenciaService.actualizar(id, dto);
            return ResponseEntity.ok(incidencia);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambioEstadoRequest request) {
        try {
            Incidencia incidencia = (Incidencia) incidenciaService.cambiarEstado(
                    id, request.estado()
            );

            return ResponseEntity.ok(incidencia);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        incidenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    public record CambioEstadoRequest(EstadoIncidencia estado) {
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ResponseEntity#badRequest#body#