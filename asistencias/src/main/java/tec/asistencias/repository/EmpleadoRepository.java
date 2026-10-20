package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Empleado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // Cuatro empleados activos registrados más recientemente
    List<Empleado> findTop4ByActivoTrueOrderByCreatedAtDescIdDesc();

    // Buscar empleados activos por código, nombres, apellidos o documento
    @Query("""
        SELECT e
        FROM Empleado e
        WHERE e.activo = true
          AND (
            LOWER(e.codigoEmpleado)
                LIKE LOWER(CONCAT('%', :termino, '%'))
            OR LOWER(e.nombres)
                LIKE LOWER(CONCAT('%', :termino, '%'))
            OR LOWER(e.apellidos)
                LIKE LOWER(CONCAT('%', :termino, '%'))
            OR LOWER(e.documento)
                LIKE LOWER(CONCAT('%', :termino, '%'))
            OR LOWER(CONCAT(CONCAT(e.nombres, ' '), e.apellidos))
                LIKE LOWER(CONCAT('%', :termino, '%'))
          )
        ORDER BY e.apellidos ASC, e.nombres ASC
        """)
    List<Empleado> buscarActivosPorTermino(
            @Param("termino") String termino,
            Pageable pageable
    );
}
