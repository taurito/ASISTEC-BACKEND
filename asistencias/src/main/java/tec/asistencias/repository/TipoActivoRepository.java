package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.TipoActivo;

public interface TipoActivoRepository extends JpaRepository<TipoActivo, Long> {
}
