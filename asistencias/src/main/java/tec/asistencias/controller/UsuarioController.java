package tec.asistencias.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.asistencias.dto.UsuarioRequest;
import tec.asistencias.dto.UsuarioResponse;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.service.UsuarioService;
import tec.asistencias.dto.TecnicoOptionResponse;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // LISTAR USUARIOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }
    // LISTAR TECNICOS
    @GetMapping("/tecnicos")
    public ResponseEntity<List<TecnicoOptionResponse>> listarTecnicos() {

        return ResponseEntity.ok(
                usuarioService.listarTecnicosActivos()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el usuario con ID: " + id
                        )
                );
    }

    // CREAR USUARIO
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.crear(request));
    }
}