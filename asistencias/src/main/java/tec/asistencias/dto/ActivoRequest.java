package tec.asistencias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivoRequest {

    @NotBlank(message = "El nombre del activo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar a los 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    private String modelo;

    @Size(max = 100, message = "La serie no puede supearar los 100 caracteres")
    private String serie;

    @NotBlank(message = "El codigo ativo es obligatorio")
    @Size(max = 50, message = "El codigo activo no puede superar los 50 caracteres")
    private String codigoActivo;

    @NotNull(message = "El tipo activo es obligatorio")
    private Long tipoActivoId;

    @NotNull(message = "La marca es obligatoria")
    private Long marcaId;

    @NotNull(message = "El estado de activo es obligatorio")
    private Long estadoActivoId;

    private Boolean activo;

}
