package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.HistorialEstadoAsistencia;

import java.util.List;

public interface HistorialEstadoAsistenciaRepository extends JpaRepository<HistorialEstadoAsistencia, Long> {

    List<HistorialEstadoAsistencia> findByAsistenciaIdOrderByFechaCambioAsc(Long AsistenciaId);
}
