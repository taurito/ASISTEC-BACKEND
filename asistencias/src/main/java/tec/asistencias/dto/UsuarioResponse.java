package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UsuarioResponse {

    private Long id;

    private String username;

    private Boolean activo;

    private Long empleadoId;
    private String empleadoNombre;

    private Set<String> roles;
}
