package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.HistorialEstadoAsistenciaResponse;
import tec.asistencias.service.HistorialEstadoAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/historial-estados-asistencia")
@RequiredArgsConstructor
public class HistorialEstadoAsistenciaController {

    private final HistorialEstadoAsistenciaService
            historialEstadoAsistenciaService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<HistorialEstadoAsistenciaResponse>>
    listar() {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService.listarTodos()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoAsistenciaResponse>
    buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService.buscarPorId(id)
        );
    }

    // LISTAR POR ASISTENCIA
    @GetMapping("/asistencia/{asistenciaId}")
    public ResponseEntity<List<HistorialEstadoAsistenciaResponse>>
    listarPorAsistencia(
            @PathVariable Long asistenciaId) {

        return ResponseEntity.ok(
                historialEstadoAsistenciaService
                        .listarPorAsistencia(asistenciaId)
        );
    }
}