package tec.asistencias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsignacionActivoRequest {

    @NotNull(message = "El activo es obligatorio")
    private Long activoId;

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    @Size(
            max = 255,
            message = "La observación no puede superar los 255 caracteres"
    )
    private String observacion;
}