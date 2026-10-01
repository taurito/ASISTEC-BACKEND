package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.Activo;
import tec.asistencias.service.ActivoService;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService activoService;

    @GetMapping
    public ResponseEntity<List<Activo>> listarTodos(){
        return ResponseEntity.ok(activoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activo> buscarPorId(@PathVariable Long id){
        return activoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Activo> guardar(@RequestBody Activo activo){
        return ResponseEntity.ok(activoService.guardar(activo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activo> actualizar(@PathVariable Long id, @RequestBody Activo activo){
        return activoService.buscarPorId(id)
                .map(existente ->{

                    existente.setNombre(activo.getNombre());
                    existente.setModelo(activo.getModelo());
                    existente.setSerie(activo.getSerie());
                    existente.setCodigoActivo(activo.getCodigoActivo());
                    existente.setTipoActivo(activo.getTipoActivo());
                    existente.setMarca(activo.getMarca());
                    existente.setEstadoActivo(activo.getEstadoActivo());
                    existente.setActivo(activo.getActivo());

                    return ResponseEntity.ok(activoService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){

        return activoService.buscarPorId(id)
                .map(activo -> {
                    activoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
