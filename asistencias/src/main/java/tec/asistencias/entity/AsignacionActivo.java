package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "activo_id",
            nullable = false
    )
    private Activo activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "empleado_id",
            nullable = false
    )
    private Empleado empleado;

    @Column(
            name = "fecha_asignacion",
            nullable = false
    )
    private LocalDateTime fechaAsignacion;

    @Column(
            name = "fecha_devolucion"
    )
    private LocalDateTime fechaDevolucion;

    @Column(length = 255)
    private String observacion;

    @Column(nullable = false)
    private Boolean vigente;

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

    @PrePersist
    public void prePersist() {

        LocalDateTime ahora = LocalDateTime.now();

        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = ahora;
        }

        this.createdAt = ahora;
        this.updatedAt = ahora;

        if (this.vigente == null) {
            this.vigente = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}