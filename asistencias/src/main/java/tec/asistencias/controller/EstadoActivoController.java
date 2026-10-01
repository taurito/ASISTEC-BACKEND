package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.EstadoActivo;
import tec.asistencias.service.EstadoActivoService;

import java.util.List;

@RestController
@RequestMapping("/api/estados-activo")
@RequiredArgsConstructor
public class EstadoActivoController {

    private final EstadoActivoService estadoActivoService;

    @GetMapping
    public ResponseEntity<List<EstadoActivo>> listarTodos(){
        return ResponseEntity.ok(estadoActivoService.listaTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoActivo> buscarPorId(@PathVariable Long id){
        return estadoActivoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoActivo> guardar(@RequestBody EstadoActivo estadoActivo){
        return ResponseEntity.ok(estadoActivoService.guardar(estadoActivo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoActivo> actualizar(@PathVariable Long id, @RequestBody EstadoActivo estadoActivo){
        return estadoActivoService.buscarPorId(id)
                .map(existente ->{
                    existente.setNombre(estadoActivo.getNombre());
                    existente.setDescripcion((estadoActivo.getDescripcion()));
                    existente.setActivo(estadoActivo.getActivo());

                    return ResponseEntity.ok(estadoActivoService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return estadoActivoService.buscarPorId(id)
                .map(estado->{
                    estadoActivoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
