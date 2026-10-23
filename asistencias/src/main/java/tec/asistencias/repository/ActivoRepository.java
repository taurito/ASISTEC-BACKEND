package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Activo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActivoRepository extends JpaRepository<Activo, Long> {

    List<Activo> findTop4ByActivoTrueOrderByCreatedAtDescIdDesc();

    @Query("""
    SELECT a
    FROM Activo a
    WHERE a.activo = true
      AND (
        LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
        OR LOWER(a.codigoActivo) LIKE LOWER(CONCAT('%', :termino, '%'))
        OR LOWER(a.serie) LIKE LOWER(CONCAT('%', :termino, '%'))
        OR LOWER(a.modelo) LIKE LOWER(CONCAT('%', :termino, '%'))
        OR LOWER(a.marca.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
      )
    ORDER BY a.createdAt DESC, a.id DESC
    """)
    List<Activo> buscarActivosPorTermino(
            @Param("termino") String termino,
            Pageable pageable
    );
}
