package com.tesig.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger con seguridad JWT.
 */
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
                .description("API REST para el sistema de gestión de taller electrónico.\n\n" +
                           "**Autenticación:**\n" +
                           "- Endpoints públicos: `/api/publico/**` y `/api/auth/**` NO requieren autenticación\n" +
                           "- Endpoints protegidos: Requieren token JWT\n\n" +
                           "**Cómo usar:**\n" +
                           "1. Hacer login en `/api/auth/login` con email y password\n" +
                           "2. Copiar el `accessToken` de la respuesta\n" +
                           "3. Hacer clic en el botón 'Authorize' arriba\n" +
                           "4. Pegar el token (solo el token, sin 'Bearer')\n" +
                           "5. Ahora puedes usar los endpoints protegidos\n\n" +
                           "**Usuarios de prueba:**\n" +
                           "- admin@tesig.com / Admin123! (Administrador)\n" +
                           "- tecnico1@tesig.com / Admin123! (Técnico)\n" +
                           "- recepcion@tesig.com / Admin123! (Recepcionista)")
                .contact(contact)
                .license(license);

        // Configurar seguridad JWT
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el access token JWT (sin el prefijo 'Bearer')")
                        )
                );
    }
}
