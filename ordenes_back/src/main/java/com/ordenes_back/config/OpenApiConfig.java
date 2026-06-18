package com.ordenes_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Api Sistema de ordenes de trabajo")
                        .version("1.0")
                        .description("Documentacion de la api para llevar la gestion de ordenes de trabajo"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese su token para acceder a los endpoints.")));

        // no agregamos SecurityRequirement global aqui
        // ya que permite que cada endpoint decida mediante anotaciones si necesita autenticacion o no
    }
}
