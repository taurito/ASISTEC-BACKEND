package tec.asistencias.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI asistenciaOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema de Asistencias")
                        .description(
                                "API REST para la gestión de activos, "
                                        + "empleados y asistencias técnicas."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Unidad de Informática"))
                        .license(new License()
                                .name("Uso institucional")))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Token JWT obtenido desde "
                                                        + "/api/auth/login"
                                        )
                        )
                );
    }

    @Bean
    public OpenApiCustomizer jwtSecurityCustomizer() {

        return openApi -> {

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {

                // Las rutas de la API necesitan JWT,
                // excepto el endpoint público de login.
                if (path.startsWith("/api/")
                        && !path.equals("/api/auth/login")) {

                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(
                                            new SecurityRequirement()
                                                    .addList(BEARER_AUTH)
                                    )
                            );
                }
            });
        };
    }
}