package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "empleado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "codigo_empleado",
            nullable = false,
            unique = true,
            length = 30
    )
    private String codigoEmpleado;

    @Column(
            name = "nombres",
            nullable = false,
            length = 100
    )
    private String nombres;

    @Column(
            name = "apellidos",
            nullable = false,
            length = 100
    )
    private String apellidos;

    @Column(
            name = "documento",
            nullable = false,
            unique = true,
            length = 30
    )
    private String documento;

    @Column(length = 100)
    private String cargo;

    @Column(length = 30)
    private String telefono;

    @Column(length = 150)
    private String correo;

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


    // Relación con Unidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "unidad_id",
            nullable = false
    )
    private Unidad unidad;


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