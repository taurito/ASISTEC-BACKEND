package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmpleadoResponse {

    private Long id;
    private String codigoEmpleado;
    private String nombres;
    private String apellidos;
    private String documento;
    private String cargo;
    private String telefono;
    private String correo;
    private Boolean activo;

    private Long unidadId;
    private String unidadNombre;
}
