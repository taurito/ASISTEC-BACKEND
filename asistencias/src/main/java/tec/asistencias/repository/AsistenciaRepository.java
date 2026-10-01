package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Asistencia;

import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    //Consulta una asistencia por su numero
    Optional<Asistencia> findByNumeroAsistencia(
            String numeroAsistencia
    );

    //historial de asistencias de un activo
    List<Asistencia> findByActivoIdOrderByFechaIngresoDesc(
            Long activoId
    );

    //asistencia solicitada por un empleado
    List<Asistencia> findByEmpleadoIdOrderByFechaIngresoDesc(
            Long empleadoId
    );

    //Asistencias atendidas por un tecnico
    List<Asistencia> findByTecnicoIdOrderByFechaIngresoDesc(
            Long tecnicoId
    );
}
