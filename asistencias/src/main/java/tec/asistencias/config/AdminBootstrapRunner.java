package tec.asistencias.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tec.asistencias.dto.UsuarioRequest;
import tec.asistencias.dto.UsuarioResponse;
import tec.asistencias.entity.Rol;
import tec.asistencias.repository.RolRepository;
import tec.asistencias.repository.UsuarioRepository;
import tec.asistencias.service.UsuarioService;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.username:}")
    private String username;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {

        // Por defecto, no se crean usuarios automáticamente.
        if (!enabled) {
            log.info("Inicialización del administrador desactivada.");
            return;
        }

        // Validar las credenciales configuradas.
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalStateException(
                    "Para inicializar el administrador debes configurar "
                            + "el usuario y la contraseña mediante "
                            + "variables de entorno."
            );
        }

        // BCrypt admite contraseñas de hasta 72 bytes.
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "La contraseña inicial supera los 72 bytes admitidos "
                            + "por BCrypt."
            );
        }

        // Solo permitir la inicialización si no existe ningún usuario.
        if (usuarioRepository.count() > 0) {
            log.warn(
                    "Ya existen usuarios. No se creará otro administrador "
                            + "automáticamente."
            );
            return;
        }

        // Buscar el rol ADMIN ya insertado mediante Flyway.
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el rol ADMIN en la base de datos."
                        )
                );

        if (!Boolean.TRUE.equals(rolAdmin.getActivo())) {
            throw new IllegalStateException(
                    "El rol ADMIN está inactivo."
            );
        }

        // Reutilizar UsuarioService para cifrar la contraseña
        // y aplicar las reglas de creación de usuarios.
        UsuarioRequest request = new UsuarioRequest();

        request.setUsername(username);
        request.setPassword(password);
        request.setEmpleadoId(null);
        request.setRolIds(Set.of(rolAdmin.getId()));

        UsuarioResponse administrador =
                usuarioService.crear(request);

        log.warn(
                "Administrador inicial '{}' creado correctamente. "
                        + "Desactiva la inicialización temporal.",
                administrador.getUsername()
        );
    }
}