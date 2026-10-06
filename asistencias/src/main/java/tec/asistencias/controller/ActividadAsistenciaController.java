package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.ActividadAsistenciaRequest;
import tec.asistencias.dto.ActividadAsistenciaResponse;
import tec.asistencias.service.ActividadAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/actividades-asistencia")
@RequiredArgsConstructor
public class ActividadAsistenciaController {

    private final ActividadAsistenciaService
            actividadAsistenciaService;

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<List<ActividadAsistenciaResponse>>
    listar() {

        return ResponseEntity.ok(
                actividadAsistenciaService.listarTodas()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ActividadAsistenciaResponse>
    buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                actividadAsistenciaService.buscarPorId(id)
        );
    }

    // LISTAR POR ASISTENCIA
    @GetMapping("/asistencia/{asistenciaId}")
    public ResponseEntity<List<ActividadAsistenciaResponse>>
    listarPorAsistencia(
            @PathVariable Long asistenciaId) {

        return ResponseEntity.ok(
                actividadAsistenciaService
                        .listarPorAsistencia(asistenciaId)
        );
    }

    // CREAR
    @PostMapping
    public ResponseEntity<ActividadAsistenciaResponse> crear(
            @Valid @RequestBody ActividadAsistenciaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        actividadAsistenciaService
                                .guardar(request)
                );
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<ActividadAsistenciaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActividadAsistenciaRequest request) {

        return ResponseEntity.ok(
                actividadAsistenciaService.actualizar(
                        id,
                        request
                )
        );
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        actividadAsistenciaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}