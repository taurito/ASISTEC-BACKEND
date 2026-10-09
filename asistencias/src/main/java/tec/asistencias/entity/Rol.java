package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "rol",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rol_nombre",
                        columnNames = "nombre"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "nombre",
            nullable = false,
            unique = true,
            length = 50
    )
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    public void prePersist() {

        if (this.activo == null) {
            this.activo = true;
        }
    }
}
