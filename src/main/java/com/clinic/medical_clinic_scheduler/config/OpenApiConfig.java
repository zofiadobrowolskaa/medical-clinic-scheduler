package com.clinic.medical_clinic_scheduler.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Admin",
                        email = "admin@clinic.com"
                ),
                description = "API Documentation for Medical Clinic Appointment Booking System",
                title = "Medical Clinic Scheduler API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8080"
                )
        },

        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBjbGluaWMuY29tIiwiaWF0IjoxNzcwNDA0NTM2LCJleHAiOjE3NzA0OTA5MzZ9.yrw68unnWw_vnYCJcn49RKjy1FSJVUEgvqPASBydhwY",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}