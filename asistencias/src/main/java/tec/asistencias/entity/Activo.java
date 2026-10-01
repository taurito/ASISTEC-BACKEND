package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "activo",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_activo_codigo",
                        columnNames = "codigo_activo"
                ),
                @UniqueConstraint(
                        name = "uk_activo_serie",
                        columnNames = "serie"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100, unique = true)
    private String serie;

    @Column(
            name = "codigo_activo",
            nullable = false,
            unique = true,
            length = 50
    )
    private String codigoActivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tipo_activo_id",
            nullable = false
    )
    private TipoActivo tipoActivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "marca_id",
            nullable = false
    )
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "estado_activo_id",
            nullable = false
    )
    private EstadoActivo estadoActivo;

    @Column(nullable = false)
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