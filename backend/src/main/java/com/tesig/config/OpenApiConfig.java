package com.tesig.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI tesigOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort + "/api");
        localServer.setDescription("Servidor de desarrollo local");

        Contact contact = new Contact();
        contact.setName("Equipo TESIG");
        contact.setEmail("contacto@tesig.com");

        License license = new License()
                .name("Propietario")
                .url("https://tesig.com/license");

        Info info = new Info()
                .title("TESIG API - Taller Electrónico Sistema Integral de Gestión")
                .version("0.1.0")
                .description("API REST para el sistema de gestión de taller electrónico. " +
                           "Incluye endpoints públicos para consulta de tickets sin autenticación " +
                           "y endpoints protegidos para el personal del taller.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
