package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.TipoActivo;
import tec.asistencias.service.TipoActivoService;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-activo")
@RequiredArgsConstructor
public class TipoActivoController {

    private final TipoActivoService tipoActivoService;

    @GetMapping
    public ResponseEntity<List<TipoActivo>> listarTodos(){
        return ResponseEntity.ok(tipoActivoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoActivo> buscarPorId(@PathVariable Long id){
        return tipoActivoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoActivo> guardar(@RequestBody TipoActivo tipoActivo){
        return ResponseEntity.ok(tipoActivoService.guardar(tipoActivo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoActivo> actualizar(@PathVariable Long id, @RequestBody TipoActivo tipoActivo){
        return tipoActivoService.buscarPorId(id)
                .map(existente -> {
                    existente.setNombre(tipoActivo.getNombre());
                    existente.setDescripcion(tipoActivo.getDescripcion());
                    existente.setActivo(tipoActivo.getActivo());

                    return ResponseEntity.ok(tipoActivoService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return tipoActivoService.buscarPorId(id)
                .map(tipo ->{
                    tipoActivoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
