package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Activo;

public interface ActivoRepository extends JpaRepository<Activo, Long> {
}
