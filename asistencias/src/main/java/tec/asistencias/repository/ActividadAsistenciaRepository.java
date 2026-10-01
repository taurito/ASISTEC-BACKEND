package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.ActividadAsistencia;

import java.util.List;

public interface ActividadAsistenciaRepository extends JpaRepository<ActividadAsistencia, Long> {

    List<ActividadAsistencia> findByAsistenciaIdOrderByFechaActividadAsc(Long asistenciaId);
}
