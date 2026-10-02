package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AsistenciaResponse {

    private Long id;
    private String numeroAsistencia;

    private Long activoId;
    private String activoNombre;
    private String codigoActivo;

    private Long empleadoId;
    private String empleadoNombre;

    private Long tecnicoId;
    private String tecnicoNombre;

    private Long tipoAsistenciaId;
    private String tipoAsistenciaNombre;

    private Long estadoAsistenciaId;
    private String estadoAsistenciaNombre;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaCierre;

    private String problemaReportado;
    private String diagnostico;
    private String trabajoRealizado;
    private String observaciones;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}