package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.ActividadAsistencia;
import tec.asistencias.service.ActividadAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/actividades-asistencia")
@RequiredArgsConstructor
public class ActividadAsistenciaController {

    private final ActividadAsistenciaService actividadAsistenciaService;

    @GetMapping
    public ResponseEntity<List<ActividadAsistencia>> listarTodos(){
        return ResponseEntity.ok(actividadAsistenciaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadAsistencia> buscarPorId(@PathVariable Long id){

        return actividadAsistenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/asistencia/{asistenciaId}")
    public ResponseEntity<List<ActividadAsistencia>> listarPorAsistencia(@PathVariable Long asistenciaId){

        return ResponseEntity.ok(actividadAsistenciaService.listarPorAsistencia(asistenciaId));
    }

    @PostMapping
    public ResponseEntity<ActividadAsistencia> guardar(@RequestBody ActividadAsistencia actividad){
        return ResponseEntity.ok(actividadAsistenciaService.guardar(actividad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadAsistencia> actualizar(@PathVariable Long id, @RequestBody ActividadAsistencia actividad){

        return actividadAsistenciaService.buscarPorId(id)
                .map(existente ->{
                    existente.setAsistencia(actividad.getAsistencia());
                    existente.setDescripcion(actividad.getDescripcion());
                    existente.setResultado(actividad.getResultado());
                    existente.setFechaActividad(actividad.getFechaActividad());
                    existente.setObservacion(actividad.getObservacion());

                    return ResponseEntity.ok(actividadAsistenciaService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> eliminar(@PathVariable Long id){

        return actividadAsistenciaService.buscarPorId(id)
                .map(actividad ->{
                    actividadAsistenciaService.eliminar(id);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
