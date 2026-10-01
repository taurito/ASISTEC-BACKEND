package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "asistencia",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_asistencia_numero",
                        columnNames = "numero_asistencia"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "numero_asistencia",
            nullable = false,
            unique = true,
            length = 30
    )
    private String numeroAsistencia;

    //Equipo que recibio la sistencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "activo_id",
            nullable = false
    )
    private Activo activo;

    //Empleado que solicita o entrega el equipo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "empleado_id",
            nullable = false
    )
    private Empleado empleado;

    //Tecnico que realiza la asistencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tecnico_id",
            nullable = false
    )
    private Empleado tecnico;

    //tipo de trabajo realizado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tipo_asistencia_id",
            nullable = false
    )
    private TipoAsistencia tipoAsistencia;

    //Estado actual de la asistencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "estado_asistencia_id",
            nullable = false
    )
    private EstadoAsistencia estadoAsistencia;

    @Column(
            name = "fecha_ingreso",
            nullable = false
    )
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "problema_reportado", columnDefinition = "TEXT")
    private String problemaReportado;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "trabajo_realizado", columnDefinition = "TEXT")
    private String trabajoRealizado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

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

        if (this.fechaIngreso == null) {
            this.fechaIngreso = ahora;
        }

        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}