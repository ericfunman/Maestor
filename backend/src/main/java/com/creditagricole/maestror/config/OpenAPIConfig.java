package com.creditagricole.maestror.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI maestrorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Maestror API")
                        .description("API de gestion des risques opérationnels pour Crédit Agricole")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Credit Agricole")
                                .email("support@creditagricole.fr"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.creditagricole.fr")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://maestror.creditagricole.fr")
                                .description("Production Server")
                ));
    }
}
