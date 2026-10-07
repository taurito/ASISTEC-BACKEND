package tec.asistencias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsistenciaUpdateRequest {

    @NotNull(message = "El técnico es obligatorio")
    private Long tecnicoId;

    @NotNull(message = "El tipo de asistencia es obligatorio")
    private Long tipoAsistenciaId;

    @Size(
            max = 10000,
            message = "El problema reportado es demasiado largo"
    )
    private String problemaReportado;

    @Size(
            max = 10000,
            message = "El diagnóstico es demasiado largo"
    )
    private String diagnostico;

    @Size(
            max = 10000,
            message = "El trabajo realizado es demasiado largo"
    )
    private String trabajoRealizado;

    @Size(
            max = 10000,
            message = "Las observaciones son demasiado largas"
    )
    private String observaciones;
}