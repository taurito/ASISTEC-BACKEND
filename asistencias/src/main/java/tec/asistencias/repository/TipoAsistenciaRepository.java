package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.TipoAsistencia;

public interface TipoAsistenciaRepository extends JpaRepository<TipoAsistencia, Long> {
}
