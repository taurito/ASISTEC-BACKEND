package tec.asistencias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadoRequest {

    @NotBlank(message = "El código de esmpledo es obligatorio")
    @Size(max = 30, message = "El código de empleado no puede superar los 30 caracteres")
    private String codigoEmpleado;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los appellidos son obligatorios")
    @Size(max = 100, message = "Los apellido no pueden superar los 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El Documento es obligatorio")
    @Size(max = 30, message = "El documento no puede superar los 30 caracteres")
    private String documento;

    @Size(max = 100, message = "El cargo no puede superar los 100 caracteres")
    private String cargo;

    @Size(max = 30, message = "El numero de telefono no puede superar los 30 caracteres")
    private String telefono;

    @Email(message = "El correo electronico no tiene un formato valido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @NotNull(message = "La unidad es obligatoria")
    private Long unidadId;
}
