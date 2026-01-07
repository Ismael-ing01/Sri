package com.backend.sri.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .components(new Components()
                                                .addSecuritySchemes("bearer-key", createAPIKeyScheme()))
                                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                                                .addList("bearer-key"))
                                .info(new Info()
                                                .title("SRI Facturacion API")
                                                .version("1.0")
                                                .description("API for SRI Electronic Invoicing with JWT Security"));
        }

        private SecurityScheme createAPIKeyScheme() {
                return new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer");
        }
}
