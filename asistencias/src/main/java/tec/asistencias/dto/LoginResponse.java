package tec.asistencias.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class LoginResponse {

    private String tokenType;
    private String accessToken;
    private long expiresIn;

    private Long usuarioId;
    private String username;
    private Long empleadoId;

    private Set<String> roles;
}