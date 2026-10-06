package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.dto.HistorialEstadoAsistenciaResponse;
import tec.asistencias.entity.HistorialEstadoAsistencia;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.HistorialEstadoAsistenciaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialEstadoAsistenciaService {

    private final HistorialEstadoAsistenciaRepository historialEstadoAsistenciaRepository;

    // LISTAR TODOS
    public List<HistorialEstadoAsistenciaResponse> listarTodos() {

        return historialEstadoAsistenciaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // BUSCAR POR ID
    public HistorialEstadoAsistenciaResponse  buscarPorId(Long id){

        HistorialEstadoAsistencia historial = historialEstadoAsistenciaRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el historial de estado con ID" + id
                        ));
        return convertirAResponse(historial);
    }

    public List<HistorialEstadoAsistenciaResponse> listarPorAsistencia(Long asistenciaId){
        return historialEstadoAsistenciaRepository
                .findByAsistenciaIdOrderByFechaCambioAsc(asistenciaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // CONVERTIR ENTITY → RESPONSE
    private HistorialEstadoAsistenciaResponse convertirAResponse(
            HistorialEstadoAsistencia historial) {

        return HistorialEstadoAsistenciaResponse.builder()
                .id(historial.getId())

                .asistenciaId(
                        historial.getAsistencia().getId()
                )

                .numeroAsistencia(
                        historial.getAsistencia()
                                .getNumeroAsistencia()
                )

                .estadoAsistenciaId(
                        historial.getEstadoAsistencia().getId()
                )

                .estadoAsistenciaNombre(
                        historial.getEstadoAsistencia().getNombre()
                )

                .fechaCambio(
                        historial.getFechaCambio()
                )

                .comentario(
                        historial.getComentario()
                )

                .createdAt(
                        historial.getCreatedAt()
                )

                .build();
    }
}
