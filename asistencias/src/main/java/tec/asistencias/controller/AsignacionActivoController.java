package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.AsignacionActivoRequest;
import tec.asistencias.dto.AsignacionActivoResponse;
import tec.asistencias.service.AsignacionActivoService;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones-activo")
@RequiredArgsConstructor
public class AsignacionActivoController {

    private final AsignacionActivoService
            asignacionActivoService;

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<List<AsignacionActivoResponse>>
    listar() {

        return ResponseEntity.ok(
                asignacionActivoService.listarTodas()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionActivoResponse>
    buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                asignacionActivoService.buscarPorId(id)
        );
    }

    // ASIGNACION VIGENTE
    @GetMapping("/activo/{activoId}/actual")
    public ResponseEntity<AsignacionActivoResponse>
    buscarAsignacionActual(
            @PathVariable Long activoId) {

        return asignacionActivoService
                .buscarAsignacionActual(activoId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // HISTORIAL
    @GetMapping("/activo/{activoId}/historial")
    public ResponseEntity<List<AsignacionActivoResponse>>
    listarHistorialPorActivo(
            @PathVariable Long activoId) {

        return ResponseEntity.ok(
                asignacionActivoService
                        .listarHistorialPorActivo(activoId)
        );
    }

    // NUEVA ASIGNACION
    @PostMapping
    public ResponseEntity<AsignacionActivoResponse> asignar(
            @Valid @RequestBody AsignacionActivoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        asignacionActivoService.asignar(request)
                );
    }

    // DEVOLVER / CERRAR ASIGNACION
    @PatchMapping("/{id}/devolver")
    public ResponseEntity<AsignacionActivoResponse> devolver(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                asignacionActivoService.devolver(id)
        );
    }
}