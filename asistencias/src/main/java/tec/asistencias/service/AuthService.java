package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import tec.asistencias.dto.LoginRequest;
import tec.asistencias.dto.LoginResponse;
import tec.asistencias.security.CustomUserDetails;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        String username = request.getUsername()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                request.getPassword()
                        )
                );

        CustomUserDetails usuario =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generarToken(usuario);

        Set<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .tokenType("Bearer")
                .accessToken(token)
                .expiresIn(jwtService.obtenerDuracionSegundos())
                .usuarioId(usuario.getUsuarioId())
                .username(usuario.getUsername())
                .empleadoId(usuario.getEmpleadoId())
                .roles(roles)
                .build();
    }
}