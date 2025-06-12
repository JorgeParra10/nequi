package com.accenture.infraestructura.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reto accenture API")
                        .description("API para la gestión de franquicias, sucursales y productos. Ejemplo de arquitectura hexagonal con Spring WebFlux.")
                        .version("1.0.0")
                );
    }

}
