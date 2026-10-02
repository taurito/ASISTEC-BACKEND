package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.EmpleadoRequest;
import tec.asistencias.dto.EmpleadoResponse;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.service.EmpleadoService;

import java.util.List;


@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listarTodos(){

        return ResponseEntity.ok(empleadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> buscarPorId(@PathVariable Long id){
        return empleadoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el empleado con ID: " + id
                        ));
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> guardar(@Valid @RequestBody EmpleadoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoRequest request) {

        return ResponseEntity.ok(
                empleadoService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        empleadoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

}
