package tec.asistencias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActividadAsistenciaRequest {

    @NotNull(message = "La asistencia es obligatoria")
    private Long asistenciaId;

    @NotBlank(message = "La descripción de la actividad es obligatoria")
    @Size(
            max = 255,
            message = "La descripción no puede superar los 255 caracteres"
    )
    private String descripcion;

    @Size(
            max = 255,
            message = "El resultado no puede superar los 255 caracteres"
    )
    private String resultado;

    @Size(
            max = 255,
            message = "La observación no puede superar los 255 caracteres"
    )
    private String observacion;
}