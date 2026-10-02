package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.ActivoRequest;
import tec.asistencias.dto.ActivoResponse;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.service.ActivoService;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService activoService;

    @GetMapping
    public ResponseEntity<List<ActivoResponse>> listarTodos(){

        return ResponseEntity.ok(activoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoResponse> buscarPorId(@PathVariable Long id){
        return activoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el activo con ID: " + id
                        )
                );
    }

    @PostMapping
    public ResponseEntity<ActivoResponse> guardar(@Valid @RequestBody ActivoRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activoService.guardar(request));
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<ActivoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActivoRequest request) {

        return ResponseEntity.ok(
                activoService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        activoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
