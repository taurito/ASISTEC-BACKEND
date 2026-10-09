package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tec.asistencias.dto.UsuarioRequest;
import tec.asistencias.dto.UsuarioResponse;
import tec.asistencias.entity.Empleado;
import tec.asistencias.entity.Rol;
import tec.asistencias.entity.Usuario;
import tec.asistencias.exception.BusinessException;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.EmpleadoRepository;
import tec.asistencias.repository.RolRepository;
import tec.asistencias.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    // LISTAR TODOS
    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // BUSCAR POR ID
    public Optional<UsuarioResponse> buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .map(this::convertirAResponse);
    }

    // CREAR
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {

        String username = normalizarUsername(
                request.getUsername()
        );

        if (usuarioRepository.existsByUsername(username)) {
            throw new BusinessException(
                    "El nombre de usuario ya está registrado."
            );
        }

        Empleado empleado = null;

        if (request.getEmpleadoId() != null) {

            empleado = empleadoRepository.findById(
                            request.getEmpleadoId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No existe el empleado con ID: "
                                            + request.getEmpleadoId()
                            )
                    );

            if (!Boolean.TRUE.equals(empleado.getActivo())) {
                throw new BusinessException(
                        "No se puede crear un usuario para un empleado inactivo."
                );
            }
        }

        Set<Rol> roles = new HashSet<>();

        for (Long rolId : request.getRolIds()) {

            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No existe el rol con ID: "
                                            + rolId
                            )
                    );

            if (!Boolean.TRUE.equals(rol.getActivo())) {
                throw new BusinessException(
                        "El rol "
                                + rol.getNombre()
                                + " está inactivo."
                );
            }

            roles.add(rol);
        }

        Usuario usuario = Usuario.builder()
                .username(username)
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .empleado(empleado)
                .activo(true)
                .roles(roles)
                .build();

        Usuario guardado =
                usuarioRepository.save(usuario);

        return convertirAResponse(guardado);
    }

    // ACTIVAR / DESACTIVAR
    @Transactional
    public UsuarioResponse cambiarEstado(
            Long id,
            Boolean activo) {

        Usuario usuario =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el usuario con ID: "
                                                + id
                                )
                        );

        usuario.setActivo(activo);

        Usuario actualizado =
                usuarioRepository.save(usuario);

        return convertirAResponse(actualizado);
    }

    // CONVERTIR ENTITY → RESPONSE
    private UsuarioResponse convertirAResponse(
            Usuario usuario) {

        String empleadoNombre = null;
        Long empleadoId = null;

        if (usuario.getEmpleado() != null) {

            empleadoId =
                    usuario.getEmpleado().getId();

            empleadoNombre =
                    usuario.getEmpleado().getNombres()
                            + " "
                            + usuario.getEmpleado().getApellidos();
        }

        Set<String> roles =
                usuario.getRoles()
                        .stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .activo(usuario.getActivo())
                .empleadoId(empleadoId)
                .empleadoNombre(empleadoNombre)
                .roles(roles)
                .build();
    }

    private String normalizarUsername(
            String username) {

        return username
                .trim() //elimina espacios
                .toLowerCase(Locale.ROOT); //convierte el nombre de usuario a minisculas
    }
}