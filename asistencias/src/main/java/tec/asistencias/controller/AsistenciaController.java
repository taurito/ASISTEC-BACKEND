package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.AsistenciaRequest;
import tec.asistencias.dto.AsistenciaResponse;
import tec.asistencias.dto.CambioEstadoAsistenciaRequest;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.service.AsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<List<AsistenciaResponse>> listar() {

        return ResponseEntity.ok(
                asistenciaService.listarTodas()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> buscarPorId(
            @PathVariable Long id) {

        return asistenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la asistencia con ID: "
                                        + id
                        )
                );
    }

    // BUSCAR POR NUMERO
    @GetMapping("/numero/{numeroAsistencia}")
    public ResponseEntity<AsistenciaResponse> buscarPorNumero(
            @PathVariable String numeroAsistencia) {

        return asistenciaService
                .buscarPorNumero(numeroAsistencia)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la asistencia con número: "
                                        + numeroAsistencia
                        )
                );
    }

    // LISTAR POR ACTIVO
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<List<AsistenciaResponse>> listarPorActivo(
            @PathVariable Long activoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorActivo(activoId)
        );
    }

    // LISTAR POR EMPLEADO
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<AsistenciaResponse>> listarPorEmpleado(
            @PathVariable Long empleadoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorEmpleado(empleadoId)
        );
    }

    // LISTAR POR TECNICO
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<AsistenciaResponse>> listarPorTecnico(
            @PathVariable Long tecnicoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorTecnico(tecnicoId)
        );
    }

    // CREAR
    @PostMapping
    public ResponseEntity<AsistenciaResponse> crear(
            @Valid @RequestBody AsistenciaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(asistenciaService.guardar(request));
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsistenciaRequest request) {

        return ResponseEntity.ok(
                asistenciaService.actualizar(
                        id,
                        request
                )
        );
    }

    // CAMBIAR ESTADO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<AsistenciaResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoAsistenciaRequest request) {

        return ResponseEntity.ok(
                asistenciaService.cambiarEstado(
                        id,
                        request
                )
        );
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        asistenciaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}