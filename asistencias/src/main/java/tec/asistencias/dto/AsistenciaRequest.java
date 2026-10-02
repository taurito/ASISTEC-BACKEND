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
public class AsistenciaRequest {

    @NotBlank(message = "El número de asistencia es obligatorio")
    @Size(max = 30, message = "El número de asistencia no puede superar los 30 caracteres")
    private String numeroAsistencia;

    @NotNull(message = "El activo es obligatorio")
    private Long activoId;

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    @NotNull(message = "El técnico es obligatorio")
    private Long tecnicoId;

    @NotNull(message = "El tipo de asistencia es obligatorio")
    private Long tipoAsistenciaId;

    @Size(max = 10000, message = "El problema reportado es demasiado largo")
    private String problemaReportado;

    @Size(max = 10000, message = "El diagnóstico es demasiado largo")
    private String diagnostico;

    @Size(max = 10000, message = "El trabajo realizado es demasiado largo")
    private String trabajoRealizado;

    @Size(max = 10000, message = "Las observaciones son demasiado largas")
    private String observaciones;
}