package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "actividad_asistencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "asistencia_id",
            nullable = false
    )
    private Asistencia asistencia;

    @Column(
            name = "descripcion",
            nullable = false,
            length = 255
    )
    private String descripcion;

    @Column(
            name = "resultado",
            length = 255
    )
    private String resultado;

    @Column(
            name = "fecha_actividad",
            nullable = false
    )
    private LocalDateTime fechaActividad;

    @Column(
            name = "observacion",
            length = 255
    )
    private String observacion;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime ahora = LocalDateTime.now();

        if (this.fechaActividad == null) {
            this.fechaActividad = ahora;
        }

        this.createdAt = ahora;
    }
}
