package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.Marca;
import tec.asistencias.service.MarcaService;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<Marca>> listarTodos(){
        return ResponseEntity.ok(marcaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable Long id){
        return marcaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Marca> guardar(@RequestBody Marca marca){
        return ResponseEntity.ok(marcaService.guardar(marca));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @RequestBody Marca marca){
        return marcaService.buscarPorId(id)
                .map(existente ->{
                    existente.setNombre(marca.getNombre());
                    existente.setDescripcion(marca.getDescripcion());
                    existente.setActivo(marca.getActivo());

                    return ResponseEntity.ok(marcaService.guardar(marca));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return marcaService.buscarPorId(id)
                .map(marca -> {
                    marcaService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
