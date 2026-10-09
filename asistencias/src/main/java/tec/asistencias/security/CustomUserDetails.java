package tec.asistencias.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.CredentialsContainer;
import tec.asistencias.entity.Rol;
import tec.asistencias.entity.Usuario;

import org.jspecify.annotations.NonNull;
import java.util.Collection;

@Getter
public class CustomUserDetails
        implements UserDetails, CredentialsContainer {

    private final Long usuarioId;
    private final Long empleadoId;

    private final String username;
    private String password;

    private final boolean activo;

    private final Collection<? extends GrantedAuthority>
            authorities;

    public CustomUserDetails(Usuario usuario) {

        this.usuarioId = usuario.getId();

        this.empleadoId =
                usuario.getEmpleado() != null
                        ? usuario.getEmpleado().getId()
                        : null;

        this.username = usuario.getUsername();
        this.password = usuario.getPassword();

        this.activo = Boolean.TRUE.equals(
                usuario.getActivo()
        );

        this.authorities =
                usuario.getRoles()
                        .stream()
                        .map(this::convertirRol)
                        .toList();
    }

    private GrantedAuthority convertirRol(Rol rol) {

        return new SimpleGrantedAuthority(
                "ROLE_" + rol.getNombre()
        );
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority>
    getAuthorities() {

        return authorities;
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public @NonNull String getUsername() {

        return username;
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return activo;
    }

    @Override
    public void eraseCredentials() {

        this.password = null;
    }
}