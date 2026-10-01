package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tec.asistencias.dto.CambioEstadoAsistenciaRequest;
import tec.asistencias.entity.Activo;
import tec.asistencias.entity.Asistencia;
import tec.asistencias.entity.EstadoAsistencia;
import tec.asistencias.entity.HistorialEstadoAsistencia;
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

    public List<Asistencia> listarTodas() {
        return asistenciaRepository.findAll();
    }

    public Optional<Asistencia> buscarPorId(Long id) {
        return asistenciaRepository.findById(id);
    }

    public Optional<Asistencia> buscarPorNumero(
            String numeroAsistencia) {

        return asistenciaRepository
                .findByNumeroAsistencia(numeroAsistencia);
    }

    public List<Asistencia> listarPorActivo(Long activoId) {
        return asistenciaRepository
                .findByActivoIdOrderByFechaIngresoDesc(activoId);
    }

    public List<Asistencia> listarPorEmpleado(Long empleadoId) {
        return asistenciaRepository
                .findByEmpleadoIdOrderByFechaIngresoDesc(empleadoId);
    }

    public List<Asistencia> listarPorTecnico(Long tecnicoId) {
        return asistenciaRepository
                .findByTecnicoIdOrderByFechaIngresoDesc(tecnicoId);
    }

    public Asistencia guardar(Asistencia asistencia) {

        /*
         * Si estamos creando una nueva asistencia,
         * verificamos que el activo no tenga otra abierta.
         */
        if (asistencia.getId() == null) {

            List<Asistencia> asistencias =
                    asistenciaRepository
                            .findByActivoIdOrderByFechaIngresoDesc(
                                    asistencia.getActivo().getId()
                            );

            boolean existeAbierta = asistencias.stream()
                    .anyMatch(a -> a.getFechaCierre() == null);

            if (existeAbierta) {
                throw new BusinessException(
                        "El activo ya tiene una asistencia abierta."
                );
            }
        }

        return asistenciaRepository.save(asistencia);
    }

    @Transactional
    public Asistencia cambiarEstado(Long asistenciaId, CambioEstadoAsistenciaRequest request){

        Asistencia asistencia = asistenciaRepository
                .findById(asistenciaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la asistencia con ID: " + asistenciaId
                        )
                );

        EstadoAsistencia nuevoEstado = estadoAsistenciaRepository
                .findById(request.getEstadoAsistenciaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el estado de asistencia con ID: "
                                        + request.getEstadoAsistenciaId()
                        ));

        String estadoActual = asistencia.getEstadoAsistencia().getNombre();
        String estadoNuevo = nuevoEstado.getNombre();

        validarTransicion(estadoActual, estadoNuevo);

        asistencia.setEstadoAsistencia(nuevoEstado);

        /*
         * Si la asistencia llega a ENTREGADO,
         * registramos automáticamente la fecha de cierre.
         */
        if("ENTREGADO".equals(estadoNuevo)){
            asistencia.setFechaCierre(LocalDateTime.now());
        }

        Asistencia asistenciaGuardada = asistenciaRepository.save(asistencia);

        /*
         * Registrar automáticamente el cambio
         * en el historial.
         */

        HistorialEstadoAsistencia historial = HistorialEstadoAsistencia.builder()
                .asistencia(asistenciaGuardada)
                .estadoAsistencia(nuevoEstado)
                .comentario(request.getComentario())
                .build();

        historialEstadoAsistenciaRepository.save(historial);

        /*
         * Actualizar el estado general del activo.
         */

        actualizarEstadoActivo(asistencia.getActivo(), estadoNuevo);

        return asistenciaGuardada;
    }

    private void validarTransicion(String estadoActual, String estadoNuevo){

        if(estadoActual.equals(estadoNuevo)){
            throw new BusinessException(
                    "La asistencia ya se encuentra en el estado "
                            + estadoActual
            );
        }

        boolean permitida = switch (estadoActual) {

            case "RECIBIDO" ->
                    estadoNuevo.equals("EN DIAGNOSTICO");

            case "EN DIAGNOSTICO" ->
                    estadoNuevo.equals("EN MANTENIMIENTO")
                            || estadoNuevo.equals("ESPERANDO REPUESTO");

            case "EN MANTENIMIENTO" ->
                    estadoNuevo.equals("ESPERANDO REPUESTO")
                            || estadoNuevo.equals("PARA ENTREGAR");

            case "ESPERANDO REPUESTO" ->
                    estadoNuevo.equals("EN MANTENIMIENTO");

            case "PARA ENTREGAR" ->
                    estadoNuevo.equals("ENTREGADO");

            default ->
                    false;
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

    private void actualizarEstadoActivo(

            Activo activo,
            String estadoAsistencia) {

        /*
         * Cuando el equipo entra al proceso de asistencia,
         * pasa a MANTENIMIENTO.
         */
        if ("RECIBIDO".equals(estadoAsistencia)
                || "EN DIAGNOSTICO".equals(estadoAsistencia)
                || "EN MANTENIMIENTO".equals(estadoAsistencia)
                || "ESPERANDO REPUESTO".equals(estadoAsistencia)) {

            estadoActivoRepository
                    .findByNombre("MANTENIMIENTO")
                    .ifPresent(activo::setEstadoActivo);

            activoRepository.save(activo);
        }

        /*
         * Cuando se entrega el equipo,
         * vuelve al estado ACTIVO.
         */
        if ("ENTREGADO".equals(estadoAsistencia)) {

            estadoActivoRepository
                    .findByNombre("ACTIVO")
                    .ifPresent(activo::setEstadoActivo);

            activoRepository.save(activo);
        }
    }

    public void eliminar(Long id) {
        asistenciaRepository.deleteById(id);
    }
}
