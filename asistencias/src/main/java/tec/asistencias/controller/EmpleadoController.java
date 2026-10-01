package tec.asistencias.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.entity.Empleado;
import tec.asistencias.service.EmpleadoService;

import java.util.List;


@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> listarTodos(){
        return ResponseEntity.ok(empleadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> buscarPorId(@PathVariable Long id){
        return empleadoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empleado> guardar(@RequestBody Empleado empleado){
        return ResponseEntity.ok(empleadoService.guardar(empleado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizar(@PathVariable Long id, @RequestBody Empleado empleado){
        return empleadoService.buscarPorId(id)
                .map(empleadoExistente ->{
                    empleadoExistente.setCodigoEmpleado(empleado.getCodigoEmpleado());
                    empleadoExistente.setNombres(empleado.getNombres());
                    empleadoExistente.setApellidos(empleado.getApellidos());
                    empleadoExistente.setDocumento(empleado.getDocumento());
                    empleadoExistente.setCargo(empleado.getCargo());
                    empleadoExistente.setTelefono(empleado.getTelefono());
                    empleadoExistente.setCorreo(empleado.getCorreo());
                    empleadoExistente.setActivo(empleado.getActivo());
                    empleadoExistente.setUnidad(empleado.getUnidad());

                    return ResponseEntity.ok(empleadoService.guardar(empleadoExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        return empleadoService.buscarPorId(id)
                .map(empleado -> {
                    empleadoService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
