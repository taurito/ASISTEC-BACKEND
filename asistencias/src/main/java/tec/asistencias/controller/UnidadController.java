package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.Unidad;
import tec.asistencias.service.UnidadService;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
public class UnidadController {

    private final UnidadService unidadService;

    @GetMapping
    public ResponseEntity<List<Unidad>> listarTodas(){
        return ResponseEntity.ok(unidadService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidad> buscarPorId(@PathVariable Long id){
        return unidadService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Unidad> guardar(@RequestBody Unidad unidad){
        return ResponseEntity.ok(unidadService.guardar(unidad));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Unidad> actualizar(@PathVariable Long id, @RequestBody Unidad unidad){
        return unidadService.buscarPorId(id)
                .map(unidadExistente ->{
                    unidadExistente.setCodigo(unidad.getCodigo());
                    unidadExistente.setNombre(unidad.getNombre());
                    unidadExistente.setDescripcion(unidad.getDescripcion());
                    unidadExistente.setActivo(unidad.getActivo());

                    return ResponseEntity.ok(unidadService.guardar(unidadExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return unidadService.buscarPorId(id)
                .map(unidad ->{
                    unidadService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
