package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.EstadoAsistencia;
import tec.asistencias.service.EstadoAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/estados-asistencia")
@RequiredArgsConstructor
public class EstadoAsistenciaController {

    private final EstadoAsistenciaService estadoAsistenciaService;

    @GetMapping
    public ResponseEntity<List<EstadoAsistencia>> listarTodas(){
        return ResponseEntity.ok(estadoAsistenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoAsistencia> buscarPorId(@PathVariable Long id){
        return estadoAsistenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoAsistencia> guardar(@RequestBody EstadoAsistencia estadoAsistencia){
        return ResponseEntity.ok(estadoAsistenciaService.guardar(estadoAsistencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoAsistencia> actualizar(@PathVariable Long id, @RequestBody EstadoAsistencia estadoAsistencia){
        return estadoAsistenciaService.buscarPorId(id)
                .map(existente ->{
                    existente.setNombre(estadoAsistencia.getNombre());
                    existente.setDescripcion(estadoAsistencia.getDescripcion());
                    existente.setActivo(estadoAsistencia.getActivo());

                    return ResponseEntity.ok(estadoAsistenciaService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return estadoAsistenciaService.buscarPorId(id)
                .map(estado ->{
                    estadoAsistenciaService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
