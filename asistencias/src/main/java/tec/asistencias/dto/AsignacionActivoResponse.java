package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AsignacionActivoResponse {

    private Long id;

    private Long activoId;
    private String activoNombre;
    private String codigoActivo;

    private Long empleadoId;
    private String empleadoNombre;

    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;

    private String observacion;

    private Boolean vigente;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}