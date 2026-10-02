package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.dto.ActivoRequest;
import tec.asistencias.dto.ActivoResponse;
import tec.asistencias.entity.Activo;
import tec.asistencias.entity.EstadoActivo;
import tec.asistencias.entity.Marca;
import tec.asistencias.entity.TipoActivo;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.ActivoRepository;
import tec.asistencias.repository.EstadoActivoRepository;
import tec.asistencias.repository.MarcaRepository;
import tec.asistencias.repository.TipoActivoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final MarcaRepository marcaRepository;
    private final EstadoActivoRepository estadoActivoRepository;

    public List<ActivoResponse> listarTodos(){

        return activoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public Optional<ActivoResponse> buscarPorId(Long id){

        return activoRepository.findById(id)
                .map(this::convertirAResponse);
    }

    public ActivoResponse guardar(ActivoRequest request){

        TipoActivo tipoActivo = tipoActivoRepository
                .findById(request.getTipoActivoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el tipo de activo con ID: "
                                        + request.getTipoActivoId()
                        )
                );

        Marca marca = marcaRepository
                .findById(request.getMarcaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la marca con ID: "
                                        + request.getMarcaId()
                        )
                );

        EstadoActivo estadoActivo = estadoActivoRepository
                .findById(request.getEstadoActivoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el estado de activo con ID: "
                                        + request.getEstadoActivoId()
                        )
                );

        Activo activo = Activo.builder()
                .nombre(request.getNombre())
                .modelo(request.getModelo())
                .serie(request.getSerie())
                .codigoActivo(request.getCodigoActivo())
                .tipoActivo(tipoActivo)
                .marca(marca)
                .estadoActivo(estadoActivo)
                .activo(
                        request.getActivo() != null
                                ? request.getActivo()
                                : true
                )
                .build();

        Activo guardado = activoRepository.save(activo);

        return convertirAResponse(guardado);
    }

    // ACTUALIZAR
    public ActivoResponse actualizar(
            Long id,
            ActivoRequest request) {

        Activo activo = activoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el activo con ID: " + id
                        )
                );

        TipoActivo tipoActivo = tipoActivoRepository
                .findById(request.getTipoActivoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el tipo de activo con ID: "
                                        + request.getTipoActivoId()
                        )
                );

        Marca marca = marcaRepository
                .findById(request.getMarcaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la marca con ID: "
                                        + request.getMarcaId()
                        )
                );

        EstadoActivo estadoActivo = estadoActivoRepository
                .findById(request.getEstadoActivoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el estado de activo con ID: "
                                        + request.getEstadoActivoId()
                        )
                );

        activo.setNombre(request.getNombre());
        activo.setModelo(request.getModelo());
        activo.setSerie(request.getSerie());
        activo.setCodigoActivo(request.getCodigoActivo());
        activo.setTipoActivo(tipoActivo);
        activo.setMarca(marca);
        activo.setEstadoActivo(estadoActivo);

        if (request.getActivo() != null) {
            activo.setActivo(request.getActivo());
        }

        Activo actualizado = activoRepository.save(activo);

        return convertirAResponse(actualizado);
    }

    public void eliminar(Long id){
        if (!activoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No existe el activo con ID: " + id
            );
        }

        activoRepository.deleteById(id);
    }

    // CONVERTIR ENTIDAD A RESPONSE
    private ActivoResponse convertirAResponse(Activo activo) {

        return ActivoResponse.builder()
                .id(activo.getId())
                .nombre(activo.getNombre())
                .modelo(activo.getModelo())
                .serie(activo.getSerie())
                .codigoActivo(activo.getCodigoActivo())
                .activo(activo.getActivo())

                .tipoActivoId(activo.getTipoActivo().getId())
                .tipoActivoNombre(activo.getTipoActivo().getNombre())

                .marcaId(activo.getMarca().getId())
                .marcaNombre(activo.getMarca().getNombre())

                .estadoActivoId(activo.getEstadoActivo().getId())
                .estadoActivoNombre(
                        activo.getEstadoActivo().getNombre()
                )

                .build();
    }
}
