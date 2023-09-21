package com.proyectozoo.zoo.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Mi API",
        version = "1.0",
        description = "Esta api permite gestionar un zoo, proporcionando diferentes operaciones CRUD sobre todas las entidades de la base de datos",
        contact = @Contact(
                name = "Fernando Gil González",
                email = "fernandoesmr@gmail.com",
                url = "https://fernandodam22.github.io/"
        )
))
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("API")
                .pathsToMatch("/api/**")
                .build();
    }
}
