package tec.asistencias.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CambioEstadoAsistenciaRequest {

    @NotNull(message = "El estado es obligatorio")
    private Long estadoAsistenciaId;

    private String comentario;
}
