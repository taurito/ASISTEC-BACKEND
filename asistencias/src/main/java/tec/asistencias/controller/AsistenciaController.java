package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.CambioEstadoAsistenciaRequest;
import tec.asistencias.entity.Asistencia;
import tec.asistencias.service.AsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @GetMapping
    public ResponseEntity<List<Asistencia>> listarTodas() {

        return ResponseEntity.ok(
                asistenciaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> buscarPorId(
            @PathVariable Long id) {

        return asistenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroAsistencia}")
    public ResponseEntity<Asistencia> buscarPorNumero(
            @PathVariable String numeroAsistencia) {

        return asistenciaService
                .buscarPorNumero(numeroAsistencia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<List<Asistencia>> listarPorActivo(
            @PathVariable Long activoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorActivo(activoId)
        );
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Asistencia>> listarPorEmpleado(
            @PathVariable Long empleadoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorEmpleado(empleadoId)
        );
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<Asistencia>> listarPorTecnico(
            @PathVariable Long tecnicoId) {

        return ResponseEntity.ok(
                asistenciaService.listarPorTecnico(tecnicoId)
        );
    }

    @PostMapping
    public ResponseEntity<Asistencia> guardar(
            @RequestBody Asistencia asistencia) {

        return ResponseEntity.ok(
                asistenciaService.guardar(asistencia)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asistencia> actualizar(
            @PathVariable Long id,
            @RequestBody Asistencia asistencia) {

        return asistenciaService.buscarPorId(id)
                .map(existente -> {

                    existente.setNumeroAsistencia(
                            asistencia.getNumeroAsistencia()
                    );

                    existente.setActivo(
                            asistencia.getActivo()
                    );

                    existente.setEmpleado(
                            asistencia.getEmpleado()
                    );

                    existente.setTecnico(
                            asistencia.getTecnico()
                    );

                    existente.setTipoAsistencia(
                            asistencia.getTipoAsistencia()
                    );

                    existente.setFechaIngreso(
                            asistencia.getFechaIngreso()
                    );

                    existente.setFechaCierre(
                            asistencia.getFechaCierre()
                    );

                    existente.setProblemaReportado(
                            asistencia.getProblemaReportado()
                    );

                    existente.setDiagnostico(
                            asistencia.getDiagnostico()
                    );

                    existente.setTrabajoRealizado(
                            asistencia.getTrabajoRealizado()
                    );

                    existente.setObservaciones(
                            asistencia.getObservaciones()
                    );

                    return ResponseEntity.ok(
                            asistenciaService.guardar(existente)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Asistencia> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoAsistenciaRequest request) {

        return ResponseEntity.ok(
                asistenciaService.cambiarEstado(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        return asistenciaService.buscarPorId(id)
                .map(asistencia -> {

                    asistenciaService.eliminar(id);

                    return ResponseEntity.noContent()
                            .<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
