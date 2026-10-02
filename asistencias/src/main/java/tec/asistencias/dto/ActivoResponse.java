package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivoResponse {

    private Long id;
    private String nombre;
    private String modelo;
    private String serie;
    private String codigoActivo;
    private Boolean activo;

    private Long tipoActivoId;
    private String tipoActivoNombre;

    private Long marcaId;
    private String marcaNombre;

    private Long estadoActivoId;
    private String estadoActivoNombre;
}