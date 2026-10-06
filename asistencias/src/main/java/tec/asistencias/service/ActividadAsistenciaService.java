package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tec.asistencias.dto.ActividadAsistenciaRequest;
import tec.asistencias.dto.ActividadAsistenciaResponse;
import tec.asistencias.entity.ActividadAsistencia;
import tec.asistencias.entity.Asistencia;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.ActividadAsistenciaRepository;
import tec.asistencias.repository.AsistenciaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadAsistenciaService {

    private final ActividadAsistenciaRepository
            actividadAsistenciaRepository;

    private final AsistenciaRepository asistenciaRepository;

    // LISTAR TODAS
    public List<ActividadAsistenciaResponse> listarTodas() {

        return actividadAsistenciaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // BUSCAR POR ID
    public ActividadAsistenciaResponse buscarPorId(Long id) {

        ActividadAsistencia actividad =
                actividadAsistenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la actividad con ID: "
                                                + id
                                )
                        );

        return convertirAResponse(actividad);
    }

    // LISTAR POR ASISTENCIA
    public List<ActividadAsistenciaResponse>
    listarPorAsistencia(Long asistenciaId) {

        return actividadAsistenciaRepository
                .findByAsistenciaIdOrderByFechaActividadAsc(asistenciaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // CREAR
    @Transactional
    public ActividadAsistenciaResponse guardar(
            ActividadAsistenciaRequest request) {

        Asistencia asistencia =
                asistenciaRepository.findById(
                                request.getAsistenciaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la asistencia con ID: "
                                                + request.getAsistenciaId()
                                )
                        );

        ActividadAsistencia actividad =
                ActividadAsistencia.builder()
                        .asistencia(asistencia)
                        .descripcion(request.getDescripcion())
                        .resultado(request.getResultado())
                        .observacion(request.getObservacion())
                        .build();

        ActividadAsistencia guardada =
                actividadAsistenciaRepository.save(actividad);

        return convertirAResponse(guardada);
    }

    // ACTUALIZAR
    @Transactional
    public ActividadAsistenciaResponse actualizar(
            Long id,
            ActividadAsistenciaRequest request) {

        ActividadAsistencia actividad =
                actividadAsistenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la actividad con ID: "
                                                + id
                                )
                        );

        Asistencia asistencia =
                asistenciaRepository.findById(
                                request.getAsistenciaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la asistencia con ID: "
                                                + request.getAsistenciaId()
                                )
                        );

        actividad.setAsistencia(asistencia);
        actividad.setDescripcion(
                request.getDescripcion()
        );
        actividad.setResultado(
                request.getResultado()
        );
        actividad.setObservacion(
                request.getObservacion()
        );

        ActividadAsistencia actualizada =
                actividadAsistenciaRepository.save(actividad);

        return convertirAResponse(actualizada);
    }

    // ELIMINAR
    public void eliminar(Long id) {

        if (!actividadAsistenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No existe la actividad con ID: " + id
            );
        }

        actividadAsistenciaRepository.deleteById(id);
    }

    // CONVERTIR ENTITY → RESPONSE
    private ActividadAsistenciaResponse convertirAResponse(
            ActividadAsistencia actividad) {

        return ActividadAsistenciaResponse.builder()
                .id(actividad.getId())

                .asistenciaId(
                        actividad.getAsistencia().getId()
                )

                .numeroAsistencia(
                        actividad.getAsistencia()
                                .getNumeroAsistencia()
                )

                .descripcion(
                        actividad.getDescripcion()
                )

                .resultado(
                        actividad.getResultado()
                )

                .fechaActividad(
                        actividad.getFechaActividad()
                )

                .observacion(
                        actividad.getObservacion()
                )

                .createdAt(
                        actividad.getCreatedAt()
                )

                .build();
    }
}