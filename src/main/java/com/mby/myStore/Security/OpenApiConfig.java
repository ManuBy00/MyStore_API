package com.mby.myStore.Security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MyStore BarberShop API",
                version = "1.0",
                description = "### Sistema de Gestión Integral para comercios\n\n" +
                        "Esta API proporciona los servicios necesarios para la gestión automatizada de todo tipo de comercios que requieran gestión de citas. " +
                        "Permite la administración de clientes, empleados y una agenda de citas inteligente con detección de conflictos.\n\n" +
                        "**Características principales:**\n" +
                        "* **Seguridad Robusta:** Implementación de autenticación basada en **JSON Web Tokens (JWT)**.\n" +
                        "* **Gestión de Agenda:** Algoritmos de validación de disponibilidad para evitar solapamientos de turnos.\n" +
                        "* **Arquitectura REST:** Endpoints estandarizados con respuestas en formato JSON y códigos de estado HTTP precisos.\n" +
                        "* **Persistencia:** Integración con base de datos **MySQL** desplegada en contenedores **Docker**.\n\n" +
                        "--- \n" +
                        "Para probar los endpoints protegidos, obtenga un token mediante el servicio de Login y utilícelo en el botón **'Authorize'** superior.",
                contact = @Contact(
                        name = "Soporte de Desarrollo - [Manuel Blancat Yuste]",
                        email = "manuelblancat00@gmail.com"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Aplica seguridad global
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}