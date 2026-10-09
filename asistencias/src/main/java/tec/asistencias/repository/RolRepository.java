package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}