package com.migros.couriertracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI courierTrackingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Migros Courier Tracking API")
                        .description("""
                                Real-time courier location tracking service for Migros.
                                
                                **Features:**
                                - Stream courier GPS coordinates in real-time
                                - Auto-detect when couriers enter 100m radius of Migros stores
                                - Prevent duplicate entry logs within 1-minute cooldown
                                - Calculate total travel distance using the Haversine formula
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Migros Tech")
                                .email("tech@migros.com.tr"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")
                ));
    }
}
