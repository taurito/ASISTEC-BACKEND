package tec.asistencias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tec.asistencias.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
