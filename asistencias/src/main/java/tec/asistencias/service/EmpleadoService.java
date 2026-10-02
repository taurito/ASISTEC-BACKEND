package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tec.asistencias.dto.EmpleadoRequest;
import tec.asistencias.dto.EmpleadoResponse;
import tec.asistencias.entity.Empleado;
import tec.asistencias.entity.Unidad;
import tec.asistencias.exception.ResourceNotFoundException;
import tec.asistencias.repository.EmpleadoRepository;
import tec.asistencias.repository.UnidadRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UnidadRepository unidadRepository;

    public List<EmpleadoResponse> listarTodos(){

        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public Optional<EmpleadoResponse> buscarPorId(Long id){

        return empleadoRepository.findById(id)
                .map(this::convertirAResponse);
    }

    public EmpleadoResponse guardar(EmpleadoRequest request){

        Unidad unidad = unidadRepository.findById(request.getUnidadId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la unidad con ID:" + request.getUnidadId()
                        ));

        Empleado empleado = Empleado.builder()
                .codigoEmpleado(request.getCodigoEmpleado())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .documento(request.getDocumento())
                .cargo(request.getCargo())
                .telefono(request.getTelefono())
                .correo(request.getCorreo())
                .unidad(unidad)
                .activo(true)
                .build();

        Empleado guardado = empleadoRepository.save(empleado);

        return convertirAResponse(guardado);
    }

    public EmpleadoResponse actualizar(
            Long id,
            EmpleadoRequest request) {

        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el empleado con ID: " + id
                        )
                );

        Unidad unidad = unidadRepository.findById(request.getUnidadId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la unidad con ID: "
                                        + request.getUnidadId()
                        )
                );

        empleado.setCodigoEmpleado(request.getCodigoEmpleado());
        empleado.setNombres(request.getNombres());
        empleado.setApellidos(request.getApellidos());
        empleado.setDocumento(request.getDocumento());
        empleado.setCargo(request.getCargo());
        empleado.setTelefono(request.getTelefono());
        empleado.setCorreo(request.getCorreo());
        empleado.setUnidad(unidad);

        Empleado actualizado = empleadoRepository.save(empleado);

        return convertirAResponse(actualizado);
    }
    public void eliminar(Long id) {

        if (!empleadoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No existe el empleado con ID: " + id
            );
        }

        empleadoRepository.deleteById(id);
    }

    // CONVERTIR ENTIDAD A RESPONSE
    private EmpleadoResponse convertirAResponse(Empleado empleado) {

        return EmpleadoResponse.builder()
                .id(empleado.getId())
                .codigoEmpleado(empleado.getCodigoEmpleado())
                .nombres(empleado.getNombres())
                .apellidos(empleado.getApellidos())
                .documento(empleado.getDocumento())
                .cargo(empleado.getCargo())
                .telefono(empleado.getTelefono())
                .correo(empleado.getCorreo())
                .activo(empleado.getActivo())
                .unidadId(empleado.getUnidad().getId())
                .unidadNombre(empleado.getUnidad().getNombre())
                .build();
    }
}
