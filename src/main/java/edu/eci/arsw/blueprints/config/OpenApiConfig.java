package edu.eci.arsw.blueprints.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(" ARSW Blueprints API")
                        .description("API RESTful para gestionar planos arquitectónicos con persistencia en PostgreSQL")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("ARSW Lab 4 - Equipo")
                                .email("equipo@arsw.edu")
                                .url("https://github.com/arsw/blueprints"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo - Docker local"),
                        new Server()
                                .url("https://api.blueprints.com")
                                .description("Servidor de producción")))
                .tags(List.of(
                        new Tag()
                                .name("Blueprints")
                                .description("Operaciones CRUD para gestión de planos"),
                        new Tag()
                                .name("Authors")
                                .description("Consultas de planos por autor")));
    }
}
