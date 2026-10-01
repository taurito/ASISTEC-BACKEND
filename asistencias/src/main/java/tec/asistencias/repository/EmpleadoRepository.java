package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
