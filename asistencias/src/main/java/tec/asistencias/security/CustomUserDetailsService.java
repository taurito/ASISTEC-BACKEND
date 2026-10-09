package tec.asistencias.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tec.asistencias.entity.Usuario;
import tec.asistencias.repository.UsuarioRepository;

import java.util.Locale;
import org.jspecify.annotations.NonNull;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        String usernameNormalizado =
                username
                        .trim()
                        .toLowerCase(Locale.ROOT);

        Usuario usuario =
                usuarioRepository
                        .findByUsername(usernameNormalizado)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuario no encontrado"
                                )
                        );

        return new CustomUserDetails(usuario);
    }
}