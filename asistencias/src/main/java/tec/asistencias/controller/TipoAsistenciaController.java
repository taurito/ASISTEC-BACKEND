package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.TipoAsistencia;
import tec.asistencias.service.TipoAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-asistencia")
@RequiredArgsConstructor
public class TipoAsistenciaController {

    private final TipoAsistenciaService tipoAsistenciaService;

    @GetMapping
    public ResponseEntity<List<TipoAsistencia>> listaTodas(){
        return ResponseEntity.ok(tipoAsistenciaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAsistencia> buscarPorId(@PathVariable Long id){

        return tipoAsistenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoAsistencia> guardar(@RequestBody TipoAsistencia tipoAsistencia){
        return ResponseEntity.ok(tipoAsistenciaService.guardar(tipoAsistencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoAsistencia> actualizar(@PathVariable Long id, @RequestBody TipoAsistencia tipoAsistencia){

        return tipoAsistenciaService.buscarPorId(id)
                .map(existente ->{
                    existente.setNombre(tipoAsistencia.getNombre());
                    existente.setDescripcion(tipoAsistencia.getDescripcion());
                    existente.setActivo(tipoAsistencia.getActivo());

                    return ResponseEntity.ok(tipoAsistenciaService.guardar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> eliminar(@PathVariable Long id){

        return tipoAsistenciaService.buscarPorId(id)
                .map(tipo ->{
                    tipoAsistenciaService.eliminar(id);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
