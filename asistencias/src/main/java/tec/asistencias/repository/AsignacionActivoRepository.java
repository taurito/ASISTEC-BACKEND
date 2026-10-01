package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.AsignacionActivo;

import java.util.List;
import java.util.Optional;

public interface AsignacionActivoRepository extends JpaRepository<AsignacionActivo, Long> {

    Optional<AsignacionActivo> findByActivoIdAndVigenteTrue(Long activoId);

    List<AsignacionActivo> findByActivoIdOrderByFechaAsignacionDesc(Long activoId);
}
