package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TecnicoOptionResponse {

    private Long usuarioId;
    private String username;

    private Long empleadoId;
    private String empleadoNombre;
    private String cargo;
}