package tec.asistencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_username",
                        columnNames = "username"
                ),
                @UniqueConstraint(
                        name = "uk_usuario_empleado",
                        columnNames = "empleado_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 50
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            length = 255
    )
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "empleado_id",
            unique = true
    )
    private Empleado empleado;

    @Column(nullable = false)
    private Boolean activo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

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