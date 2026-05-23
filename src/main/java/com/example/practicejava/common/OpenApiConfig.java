package com.example.practicejava.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Family League API")
                        .version("1.0")
                        .description("""
                                Backend API for the Family League sports prediction platform.
                                Users predict match winners, toss results, and player of the match.
                                Points are awarded for correct predictions and a leaderboard tracks rankings.

                                **Admin workflow:** Create league → Add season → Enroll teams → Schedule matches
                                → Activate season → Publish results → Close season.

                                **Authentication:** All endpoints except `/auth/register` and `/auth/login`
                                require a Bearer JWT token. Obtain a token via `POST /api/v1/auth/login`.
                                """)
                        .contact(new Contact().name("Family League Admin")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from POST /api/v1/auth/login")));
    }
}
