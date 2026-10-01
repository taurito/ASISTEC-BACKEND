package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado_asistencia")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "asistencia_id",
            nullable = false
    )
    private Asistencia asistencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "estado_asistencia_id",
            nullable = false
    )
    private EstadoAsistencia estadoAsistencia;

    @Column(
            name = "fecha_cambio",
            nullable = false
    )
    private LocalDateTime fechaCambio;

    @Column(length = 255)
    private String comentario;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime ahora = LocalDateTime.now();

        if (this.fechaCambio == null) {
            this.fechaCambio = ahora;
        }

        this.createdAt = ahora;
    }
}
