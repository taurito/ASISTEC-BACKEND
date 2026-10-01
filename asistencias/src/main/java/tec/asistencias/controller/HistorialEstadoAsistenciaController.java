package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.HistorialEstadoAsistencia;
import tec.asistencias.service.HistorialEstadoAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/historial-estados-asistencia")
@RequiredArgsConstructor
public class HistorialEstadoAsistenciaController {

    private final HistorialEstadoAsistenciaService historialEstadoAsistenciaService;

    @GetMapping
    public ResponseEntity<List<HistorialEstadoAsistencia>> listarTodos() {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoAsistencia> buscarPorId(
            @PathVariable Long id) {

        return historialEstadoAsistenciaService
                .buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/asistencia/{asistenciaId}")
    public ResponseEntity<List<HistorialEstadoAsistencia>>
    listarPorAsistencia(
            @PathVariable Long asistenciaId) {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService
                        .listarPorAsistencia(asistenciaId)
        );
    }

    @PostMapping
    public ResponseEntity<HistorialEstadoAsistencia> guardar(
            @RequestBody HistorialEstadoAsistencia historial) {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService
                        .guardar(historial)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        return historialEstadoAsistenciaService
                .buscarPorId(id)
                .map(historial -> {

                    historialEstadoAsistenciaService
                            .eliminar(id);

                    return ResponseEntity.noContent()
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}