package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tec.asistencias.dto.AsignacionActivoRequest;
import tec.asistencias.dto.AsignacionActivoResponse;
import tec.asistencias.entity.Activo;
import tec.asistencias.entity.AsignacionActivo;
import tec.asistencias.entity.Empleado;
import tec.asistencias.exception.BusinessException;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.ActivoRepository;
import tec.asistencias.repository.AsignacionActivoRepository;
import tec.asistencias.repository.EmpleadoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsignacionActivoService {

    private final AsignacionActivoRepository
            asignacionActivoRepository;

    private final ActivoRepository activoRepository;

    private final EmpleadoRepository empleadoRepository;

    // LISTAR TODAS
    public List<AsignacionActivoResponse> listarTodas() {

        return asignacionActivoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // BUSCAR POR ID
    public AsignacionActivoResponse buscarPorId(Long id) {

        AsignacionActivo asignacion =
                asignacionActivoRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la asignación con ID: "
                                                + id
                                )
                        );

        return convertirAResponse(asignacion);
    }

    // BUSCAR ASIGNACION VIGENTE DE UN ACTIVO
    public Optional<AsignacionActivoResponse>
    buscarAsignacionActual(Long activoId) {

        return asignacionActivoRepository
                .findByActivoIdAndVigenteTrue(activoId)
                .map(this::convertirAResponse);
    }

    // HISTORIAL DE UN ACTIVO
    public List<AsignacionActivoResponse>
    listarHistorialPorActivo(Long activoId) {

        return asignacionActivoRepository
                .findByActivoIdOrderByFechaAsignacionDesc(activoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // CREAR UNA NUEVA ASIGNACION
    @Transactional
    public AsignacionActivoResponse asignar(
            AsignacionActivoRequest request) {

        Activo activo = activoRepository.findById(
                        request.getActivoId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el activo con ID: "
                                        + request.getActivoId()
                        )
                );

        Empleado empleado = empleadoRepository.findById(
                        request.getEmpleadoId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el empleado con ID: "
                                        + request.getEmpleadoId()
                        )
                );

        validarActivo(activo);
        validarEmpleado(empleado);

        Optional<AsignacionActivo> asignacionActual =
                asignacionActivoRepository
                        .findByActivoIdAndVigenteTrue(
                                activo.getId()
                        );

        if (asignacionActual.isPresent()) {

            AsignacionActivo anterior =
                    asignacionActual.get();

            anterior.setVigente(false);
            anterior.setFechaDevolucion(
                    LocalDateTime.now()
            );

            asignacionActivoRepository.save(anterior);
        }

        AsignacionActivo nuevaAsignacion =
                AsignacionActivo.builder()
                        .activo(activo)
                        .empleado(empleado)
                        .observacion(request.getObservacion())
                        .vigente(true)
                        .build();

        AsignacionActivo guardada =
                asignacionActivoRepository.save(
                        nuevaAsignacion
                );

        return convertirAResponse(guardada);
    }

    // CERRAR ASIGNACION
    @Transactional
    public AsignacionActivoResponse devolver(Long id) {

        AsignacionActivo asignacion =
                asignacionActivoRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la asignación con ID: "
                                                + id
                                )
                        );

        if (!Boolean.TRUE.equals(asignacion.getVigente())) {
            throw new BusinessException(
                    "La asignación ya se encuentra cerrada."
            );
        }

        asignacion.setVigente(false);
        asignacion.setFechaDevolucion(
                LocalDateTime.now()
        );

        AsignacionActivo actualizada =
                asignacionActivoRepository.save(
                        asignacion
                );

        return convertirAResponse(actualizada);
    }

    // VALIDAR ACTIVO
    private void validarActivo(Activo activo) {

        String estado =
                activo.getEstadoActivo().getNombre();

        if ("BAJA".equals(estado)
                || "DESUSO".equals(estado)
                || "SEGURO".equals(estado)) {

            throw new BusinessException(
                    "No se puede asignar un activo en estado "
                            + estado
            );
        }
    }

    // VALIDAR EMPLEADO
    private void validarEmpleado(Empleado empleado) {

        if (!Boolean.TRUE.equals(empleado.getActivo())) {

            throw new BusinessException(
                    "No se puede asignar un activo a un empleado inactivo."
            );
        }
    }

    // CONVERTIR ENTITY → RESPONSE
    private AsignacionActivoResponse convertirAResponse(
            AsignacionActivo asignacion) {

        return AsignacionActivoResponse.builder()
                .id(asignacion.getId())

                .activoId(
                        asignacion.getActivo().getId()
                )

                .activoNombre(
                        asignacion.getActivo().getNombre()
                )

                .codigoActivo(
                        asignacion.getActivo().getCodigoActivo()
                )

                .empleadoId(
                        asignacion.getEmpleado().getId()
                )

                .empleadoNombre(
                        asignacion.getEmpleado().getNombres()
                                + " "
                                + asignacion.getEmpleado().getApellidos()
                )

                .fechaAsignacion(
                        asignacion.getFechaAsignacion()
                )

                .fechaDevolucion(
                        asignacion.getFechaDevolucion()
                )

                .observacion(
                        asignacion.getObservacion()
                )

                .vigente(
                        asignacion.getVigente()
                )

                .createdAt(
                        asignacion.getCreatedAt()
                )

                .updatedAt(
                        asignacion.getUpdatedAt()
                )

                .build();
    }
}