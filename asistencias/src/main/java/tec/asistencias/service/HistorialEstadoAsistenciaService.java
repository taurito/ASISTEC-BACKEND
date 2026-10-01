package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.HistorialEstadoAsistencia;
import tec.asistencias.repository.HistorialEstadoAsistenciaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistorialEstadoAsistenciaService {

    private final HistorialEstadoAsistenciaRepository historialEstadoAsistenciaRepository;

    public List<HistorialEstadoAsistencia> listarTodos(){
        return historialEstadoAsistenciaRepository.findAll();
    }

    public Optional<HistorialEstadoAsistencia> buscarPorId(Long id){
        return historialEstadoAsistenciaRepository.findById(id);
    }

    public List<HistorialEstadoAsistencia> listarPorAsistencia(
            Long asistenciaId) {

        return historialEstadoAsistenciaRepository
                .findByAsistenciaIdOrderByFechaCambioAsc(
                        asistenciaId
                );
    }

    public HistorialEstadoAsistencia guardar(
            HistorialEstadoAsistencia historial) {

        return historialEstadoAsistenciaRepository.save(historial);
    }

    public void eliminar(Long id) {
        historialEstadoAsistenciaRepository.deleteById(id);
    }
}
