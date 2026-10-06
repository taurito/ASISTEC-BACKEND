package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistorialEstadoAsistenciaResponse {

    private Long id;

    private Long asistenciaId;
    private String numeroAsistencia;

    private Long estadoAsistenciaId;
    private String estadoAsistenciaNombre;

    private LocalDateTime fechaCambio;

    private String comentario;

    private LocalDateTime createdAt;
}