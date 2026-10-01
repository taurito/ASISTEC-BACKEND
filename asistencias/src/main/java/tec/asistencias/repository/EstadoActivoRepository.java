package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.EstadoActivo;

import java.util.Optional;

public interface EstadoActivoRepository extends JpaRepository<EstadoActivo, Long> {
    Optional<EstadoActivo> findByNombre(String nombre);
}
