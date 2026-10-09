package tec.asistencias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(
            min = 4,
            max = 50,
            message = "El nombre de usuario debe tener entre 4 y 50 caracteres"
    )
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(
            min = 8,
            max = 100,
            message = "La contraseña debe tener entre 8 y 100 caracteres"
    )
    private String password;

    private Long empleadoId;

    @NotEmpty(message = "Debe asignarse al menos un rol")
    private Set<Long> rolIds;
}
