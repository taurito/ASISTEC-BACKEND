package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tec.asistencias.dto.AsistenciaRequest;
import tec.asistencias.dto.AsistenciaResponse;
import tec.asistencias.dto.CambioEstadoAsistenciaRequest;
import tec.asistencias.entity.*;
import tec.asistencias.repository.*;
import tec.asistencias.exception.BusinessException;
import tec.asistencias.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final EstadoAsistenciaRepository estadoAsistenciaRepository;
    private final HistorialEstadoAsistenciaRepository historialEstadoAsistenciaRepository;
    private final EstadoActivoRepository estadoActivoRepository;
    private final ActivoRepository activoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final TipoAsistenciaRepository tipoAsistenciaRepository;

    public List<AsistenciaResponse> listarTodas() {

        return asistenciaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public Optional<AsistenciaResponse> buscarPorId(Long id) {

        return asistenciaRepository.findById(id)
                .map(this::convertirAResponse);
    }

    public Optional<AsistenciaResponse> buscarPorNumero(
            String numeroAsistencia) {

        return asistenciaRepository
                .findByNumeroAsistencia(numeroAsistencia)
                .map(this::convertirAResponse);
    }

    public List<AsistenciaResponse> listarPorActivo(Long activoId) {
        return asistenciaRepository
                .findByActivoIdOrderByFechaIngresoDesc(activoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<AsistenciaResponse> listarPorEmpleado(Long empleadoId) {
        return asistenciaRepository
                .findByEmpleadoIdOrderByFechaIngresoDesc(empleadoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<AsistenciaResponse> listarPorTecnico(Long tecnicoId) {
        return asistenciaRepository
                .findByTecnicoIdOrderByFechaIngresoDesc(tecnicoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public AsistenciaResponse guardar(AsistenciaRequest request) {
        Activo activo = activoRepository.findById(request.getActivoId())
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

        Empleado tecnico = empleadoRepository.findById(
                        request.getTecnicoId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el técnico con ID: "
                                        + request.getTecnicoId()
                        )
                );

        TipoAsistencia tipoAsistencia =
                tipoAsistenciaRepository.findById(
                                request.getTipoAsistenciaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el tipo de asistencia con ID: "
                                                + request.getTipoAsistenciaId()
                                )
                        );

        // Verificar que el activo no tenga otra asistencia abierta
        List<Asistencia> asistencias =
                asistenciaRepository
                        .findByActivoIdOrderByFechaIngresoDesc(
                                request.getActivoId()
                        );

        boolean existeAbierta = asistencias.stream()
                .anyMatch(a -> a.getFechaCierre() == null);

        if (existeAbierta) {
            throw new BusinessException(
                    "El activo ya tiene una asistencia abierta."
            );
        }

        // El sistema asigna automáticamente RECIBIDO
        EstadoAsistencia estadoRecibido =
                estadoAsistenciaRepository
                        .findByNombre("RECIBIDO")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el estado de asistencia RECIBIDO."
                                )
                        );

        Asistencia asistencia = Asistencia.builder()
                .numeroAsistencia(request.getNumeroAsistencia())
                .activo(activo)
                .empleado(empleado)
                .tecnico(tecnico)
                .tipoAsistencia(tipoAsistencia)
                .estadoAsistencia(estadoRecibido)
                .problemaReportado(request.getProblemaReportado())
                .diagnostico(request.getDiagnostico())
                .trabajoRealizado(request.getTrabajoRealizado())
                .observaciones(request.getObservaciones())
                .build();

        Asistencia guardada = asistenciaRepository.save(asistencia);

        // El activo entra a mantenimiento desde que ingresa a informática
        actualizarEstadoActivo(
                activo,
                "RECIBIDO"
        );

        // Registrar estado inicial en el historial
        HistorialEstadoAsistencia historial =
                HistorialEstadoAsistencia.builder()
                        .asistencia(guardada)
                        .estadoAsistencia(estadoRecibido)
                        .comentario("Asistencia creada")
                        .build();

        historialEstadoAsistenciaRepository.save(historial);

        return convertirAResponse(guardada);
    }

    // ACTUALIZAR
    @Transactional
    public AsistenciaResponse actualizar(
            Long id,
            AsistenciaRequest request) {

        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la asistencia con ID: " + id
                        )
                );

        Activo activo = activoRepository.findById(request.getActivoId())
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

        Empleado tecnico = empleadoRepository.findById(
                        request.getTecnicoId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el técnico con ID: "
                                        + request.getTecnicoId()
                        )
                );

        TipoAsistencia tipoAsistencia =
                tipoAsistenciaRepository.findById(
                                request.getTipoAsistenciaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el tipo de asistencia con ID: "
                                                + request.getTipoAsistenciaId()
                                )
                        );

        asistencia.setNumeroAsistencia(
                request.getNumeroAsistencia()
        );
        asistencia.setActivo(activo);
        asistencia.setEmpleado(empleado);
        asistencia.setTecnico(tecnico);
        asistencia.setTipoAsistencia(tipoAsistencia);
        asistencia.setProblemaReportado(
                request.getProblemaReportado()
        );
        asistencia.setDiagnostico(
                request.getDiagnostico()
        );
        asistencia.setTrabajoRealizado(
                request.getTrabajoRealizado()
        );
        asistencia.setObservaciones(
                request.getObservaciones()
        );

        Asistencia actualizada =
                asistenciaRepository.save(asistencia);

        return convertirAResponse(actualizada);
    }

    // CAMBIAR ESTADO
    @Transactional
    public AsistenciaResponse cambiarEstado(
            Long asistenciaId,
            CambioEstadoAsistenciaRequest request) {

        Asistencia asistencia =
                asistenciaRepository.findById(asistenciaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe la asistencia con ID: "
                                                + asistenciaId
                                )
                        );

        EstadoAsistencia nuevoEstado =
                estadoAsistenciaRepository.findById(
                                request.getEstadoAsistenciaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el estado de asistencia con ID: "
                                                + request.getEstadoAsistenciaId()
                                )
                        );

        String estadoActual =
                asistencia.getEstadoAsistencia().getNombre();

        String estadoNuevo =
                nuevoEstado.getNombre();

        validarTransicion(
                estadoActual,
                estadoNuevo
        );

        asistencia.setEstadoAsistencia(nuevoEstado);

        if ("ENTREGADO".equals(estadoNuevo)) {
            asistencia.setFechaCierre(
                    LocalDateTime.now()
            );
        }

        Asistencia asistenciaGuardada =
                asistenciaRepository.save(asistencia);

        HistorialEstadoAsistencia historial =
                HistorialEstadoAsistencia.builder()
                        .asistencia(asistenciaGuardada)
                        .estadoAsistencia(nuevoEstado)
                        .comentario(request.getComentario())
                        .build();

        historialEstadoAsistenciaRepository.save(historial);

        actualizarEstadoActivo(
                asistencia.getActivo(),
                estadoNuevo
        );

        return convertirAResponse(asistenciaGuardada);
    }

    // VALIDAR TRANSICIONES
    private void validarTransicion(
            String estadoActual,
            String estadoNuevo) {

        if (estadoActual.equals(estadoNuevo)) {
            throw new BusinessException(
                    "La asistencia ya se encuentra en el estado "
                            + estadoActual
            );
        }

        boolean permitida = switch (estadoActual) {
            case "RECIBIDO" -> estadoNuevo.equals("EN DIAGNOSTICO");
            case "EN DIAGNOSTICO" -> estadoNuevo.equals("EN MANTENIMIENTO")
                    || estadoNuevo.equals("ESPERANDO REPUESTO");
            case "EN MANTENIMIENTO" -> estadoNuevo.equals("ESPERANDO REPUESTO")
                    || estadoNuevo.equals("PARA ENTREGAR");
            case "ESPERANDO REPUESTO" -> estadoNuevo.equals("EN MANTENIMIENTO");
            case "PARA ENTREGAR" -> estadoNuevo.equals("ENTREGADO");
            default -> false;
        };

        if (!permitida) {
            throw new BusinessException(
                    "No está permitido cambiar el estado de "
                            + estadoActual
                            + " a "
                            + estadoNuevo
            );
        }
    }

    // ACTUALIZAR ESTADO DEL ACTIVO
    private void actualizarEstadoActivo(
            Activo activo,
            String estadoAsistencia) {

        if ("RECIBIDO".equals(estadoAsistencia)
                || "EN DIAGNOSTICO".equals(estadoAsistencia)
                || "EN MANTENIMIENTO".equals(estadoAsistencia)
                || "ESPERANDO REPUESTO".equals(estadoAsistencia)) {

            estadoActivoRepository
                    .findByNombre("MANTENIMIENTO")
                    .ifPresent(activo::setEstadoActivo);

            activoRepository.save(activo);
        }

        if ("ENTREGADO".equals(estadoAsistencia)) {

            estadoActivoRepository
                    .findByNombre("ACTIVO")
                    .ifPresent(activo::setEstadoActivo);

            activoRepository.save(activo);
        }
    }

    // ELIMINAR
    public void eliminar(Long id) {

        if (!asistenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No existe la asistencia con ID: " + id
            );
        }

        asistenciaRepository.deleteById(id);
    }

    // CONVERTIR ENTITY → RESPONSE
    private AsistenciaResponse convertirAResponse(
            Asistencia asistencia) {

        return AsistenciaResponse.builder()
                .id(asistencia.getId())
                .numeroAsistencia(
                        asistencia.getNumeroAsistencia()
                )

                .activoId(
                        asistencia.getActivo().getId()
                )
                .activoNombre(
                        asistencia.getActivo().getNombre()
                )
                .codigoActivo(
                        asistencia.getActivo().getCodigoActivo()
                )

                .empleadoId(
                        asistencia.getEmpleado().getId()
                )
                .empleadoNombre(
                        asistencia.getEmpleado().getNombres()
                                + " "
                                + asistencia.getEmpleado().getApellidos()
                )

                .tecnicoId(
                        asistencia.getTecnico().getId()
                )
                .tecnicoNombre(
                        asistencia.getTecnico().getNombres()
                                + " "
                                + asistencia.getTecnico().getApellidos()
                )

                .tipoAsistenciaId(
                        asistencia.getTipoAsistencia().getId()
                )
                .tipoAsistenciaNombre(
                        asistencia.getTipoAsistencia().getNombre()
                )

                .estadoAsistenciaId(
                        asistencia.getEstadoAsistencia().getId()
                )
                .estadoAsistenciaNombre(
                        asistencia.getEstadoAsistencia().getNombre()
                )

                .fechaIngreso(
                        asistencia.getFechaIngreso()
                )
                .fechaCierre(
                        asistencia.getFechaCierre()
                )

                .problemaReportado(
                        asistencia.getProblemaReportado()
                )
                .diagnostico(
                        asistencia.getDiagnostico()
                )
                .trabajoRealizado(
                        asistencia.getTrabajoRealizado()
                )
                .observaciones(
                        asistencia.getObservaciones()
                )

                .createdAt(
                        asistencia.getCreatedAt()
                )
                .updatedAt(
                        asistencia.getUpdatedAt()
                )

                .build();
    }
}
