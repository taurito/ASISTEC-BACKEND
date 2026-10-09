package tec.asistencias.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import tec.asistencias.security.CustomUserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer:asistencias-api}")
    private String issuer;

    @Value("${app.jwt.expiration-minutes:30}")
    private long expirationMinutes;

    public String generarToken(CustomUserDetails usuario) {

        Instant ahora = Instant.now();

        Instant expiracion = ahora.plus(
                expirationMinutes,
                ChronoUnit.MINUTES
        );

        List<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet.Builder claimsBuilder =
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .subject(usuario.getUsername())
                        .issuedAt(ahora)
                        .expiresAt(expiracion)
                        .claim("uid", usuario.getUsuarioId())
                        .claim("roles", roles);

        if (usuario.getEmpleadoId() != null) {
            claimsBuilder.claim(
                    "eid",
                    usuario.getEmpleadoId()
            );
        }

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        header,
                        claimsBuilder.build()
                )
        ).getTokenValue();
    }

    public long obtenerDuracionSegundos() {
        return ChronoUnit.MINUTES
                .getDuration()
                .multipliedBy(expirationMinutes)
                .getSeconds();
    }
}