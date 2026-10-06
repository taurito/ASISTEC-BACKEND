package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActividadAsistenciaResponse {

    private Long id;

    private Long asistenciaId;
    private String numeroAsistencia;

    private String descripcion;
    private String resultado;

    private LocalDateTime fechaActividad;

    private String observacion;

    private LocalDateTime createdAt;
}