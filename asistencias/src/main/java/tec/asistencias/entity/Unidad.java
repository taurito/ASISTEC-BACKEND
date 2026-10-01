package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "unidad",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_unidad_codigo",
                        columnNames = "codigo"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "codigo",
            nullable = false,
            unique = true,
            length = 20
    )
    private String codigo;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "descripcion",
            length = 255
    )
    private String descripcion;

    @Column(
            name = "activo",
            nullable = false
    )
    private Boolean activo;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    // ============================
    // MÉTODOS DEL CICLO DE VIDA
    // ============================

    @PrePersist
    public void prePersist() {

        LocalDateTime ahora = LocalDateTime.now();

        this.createdAt = ahora;
        this.updatedAt = ahora;

        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

