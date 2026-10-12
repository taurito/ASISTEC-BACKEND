package tec.asistencias.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import tec.asistencias.security.CustomUserDetailsService;
import org.springframework.security.config.Customizer;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // CONVERTIR LOS ROLES DEL JWT EN AUTORIDADES
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        Converter<Jwt, Collection<GrantedAuthority>> rolesConverter =
                jwt -> {

                    List<String> roles =
                            jwt.getClaimAsStringList("roles");

                    if (roles == null) {
                        return List.of();
                    }

                    return roles.stream()
                            .map(role ->
                                    (GrantedAuthority)
                                            new SimpleGrantedAuthority(role)
                            )
                            .toList();
                };

        converter.setJwtGrantedAuthoritiesConverter(
                rolesConverter
        );

        return converter;
    }

    // CONFIGURAR SEGURIDAD Y AUTORIZACIONES
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationConverter jwtAuthenticationConverter) {

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authenticationProvider(authenticationProvider)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        // DOCUMENTACIÓN
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // LOGIN PÚBLICO
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()

                        // ADMINISTRACIÓN DE USUARIOS
                        .requestMatchers(
                                "/api/usuarios/**"
                        ).hasRole("ADMIN")

                        // CONSULTA DE EMPLEADOS:
                        // solo administrador y técnico
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/empleados/**"
                        ).hasAnyRole("ADMIN", "TECNICO")

                        // CONSULTA DE ACTIVOS, ASISTENCIAS,
                        // ASIGNACIONES E HISTORIALES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/unidades/**",
                                "/api/tipos-activo/**",
                                "/api/marcas/**",
                                "/api/estados-activo/**",
                                "/api/activos/**",
                                "/api/tipos-asistencia/**",
                                "/api/estados-asistencia/**",
                                "/api/asignaciones-activo/**",
                                "/api/asistencias/**",
                                "/api/historial-estados-asistencia/**",
                                "/api/actividades-asistencia/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "TECNICO",
                                "CONSULTA"
                        )

                        // LOS TÉCNICOS Y ADMINISTRADORES
                        // PUEDEN REGISTRAR ACTIVIDADES
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/actividades-asistencia/**"
                        ).hasAnyRole("ADMIN", "TECNICO")

                        // LOS TÉCNICOS Y ADMINISTRADORES
                        // PUEDEN MODIFICAR ACTIVIDADES
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/actividades-asistencia/**"
                        ).hasAnyRole("ADMIN", "TECNICO")

                        // CAMBIOS DE ESTADO
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/asistencias/*/estado"
                        ).hasAnyRole("ADMIN", "TECNICO")

                        // CUALQUIER OTRA OPERACIÓN DE LA API
                        // QUEDA RESERVADA AL ADMINISTRADOR
                        .requestMatchers(
                                "/api/**"
                        ).hasRole("ADMIN")

                        // DENEGAR TODO LO NO CONTEMPLADO
                        .anyRequest().denyAll()
                )

                // VALIDAR JWT
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )

                // SIN HTTP BASIC NI FORMULARIO WEB
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // API REST SIN SESIÓN NI CSRF
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}