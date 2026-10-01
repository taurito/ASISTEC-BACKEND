package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.EstadoAsistencia;

import java.util.Optional;

public interface EstadoAsistenciaRepository extends JpaRepository<EstadoAsistencia, Long> {

    Optional<EstadoAsistencia> findByNombre(String nombre);
}
