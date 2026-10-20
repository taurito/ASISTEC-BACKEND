package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
    SELECT DISTINCT u
    FROM Usuario u
    JOIN u.roles r
    JOIN FETCH u.empleado e
    WHERE u.activo = true
      AND e.activo = true
      AND r.nombre = :rolNombre
      AND r.activo = true
    ORDER BY e.apellidos, e.nombres
    """)
    List<Usuario> findUsuariosActivosPorRolConEmpleado(
            @Param("rolNombre") String rolNombre
    );

    @Query("""
    SELECT CASE WHEN COUNT(DISTINCT u) > 0
                THEN true ELSE false END
    FROM Usuario u
    JOIN u.roles r
    WHERE u.activo = true
      AND u.empleado.id = :empleadoId
      AND u.empleado.activo = true
      AND r.nombre = :rolNombre
      AND r.activo = true
    """)
    boolean existeUsuarioActivoConRolPorEmpleado(
            @Param("empleadoId") Long empleadoId,
            @Param("rolNombre") String rolNombre
    );
}