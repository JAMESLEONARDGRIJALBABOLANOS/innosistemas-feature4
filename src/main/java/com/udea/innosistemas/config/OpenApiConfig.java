package com.udea.innosistemas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 * Define información general de la API, seguridad JWT y servidores
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "InnoSistemas Backend API",
        version = "1.0.0",
        description = "Plataforma de Integración y Desarrollo de Software para Estudiantes de Ingeniería de Sistemas",
        contact = @Contact(
            name = "Fábrica-Escuela de Software UdeA",
            email = "fabrica@udea.edu.co"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080/api/v1", description = "Servidor de desarrollo local"),
        @Server(url = "https://innosistemas.udea.edu.co/api/v1", description = "Servidor de producción")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Ingrese el token JWT obtenido del endpoint /auth/login"
)
public class OpenApiConfig {
}