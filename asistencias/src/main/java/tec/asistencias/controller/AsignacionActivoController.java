package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.AsignacionActivo;
import tec.asistencias.service.AsignacionActivoservice;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones-activo")
@RequiredArgsConstructor
public class AsignacionActivoController {

    private final AsignacionActivoservice asignacionActivoService;

    @GetMapping
    public ResponseEntity<List<AsignacionActivo>> listarTodas(){
        return ResponseEntity.ok(asignacionActivoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionActivo> buscarPorId(@PathVariable Long id){
        return asignacionActivoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activo/{activoId}/actual")
    public ResponseEntity<AsignacionActivo> buscarAsignacionActual(@PathVariable Long activoId){
        return asignacionActivoService
                .buscarAsignacionActual(activoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activo/{activoId}/historial")
    public ResponseEntity<List<AsignacionActivo>> listaHistorial(@PathVariable Long activoId){
        return ResponseEntity.ok(asignacionActivoService.listarHistorialPorActivo(activoId));
    }

    @PostMapping
    public ResponseEntity<AsignacionActivo> guardar(@RequestBody AsignacionActivo asignacion){
        return ResponseEntity.ok(asignacionActivoService.guardar(asignacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionActivo> actualizar(@PathVariable Long id, @RequestBody AsignacionActivo asignacion){
        return asignacionActivoService.buscarPorId(id)
                .map(existente ->{
                    existente.setActivo(asignacion.getActivo());
                    existente.setEmpleado(asignacion.getEmpleado());
                    existente.setFechaAsignacion(asignacion.getFechaAsignacion());
                    existente.setFechaDevolucion(asignacion.getFechaDevolucion());
                    existente.setObservacion(asignacion.getObservacion());
                    existente.setVigente(asignacion.getVigente());

                    return ResponseEntity.ok(asignacionActivoService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());

    }

    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return asignacionActivoService.buscarPorId(id)
                .map(asignacion ->{
                    asignacionActivoService.eliminar(id);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
