package com.ermis_market.couriertracking.config;

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
                        .title("Ermiş Market Courier Tracking API")
                        .description("""
                                Real-time courier location tracking service for Ermiş Market.
                                
                                **Features:**
                                - Stream courier GPS coordinates in real-time
                                - Auto-detect when couriers enter 100m radius of Ermiş Market stores
                                - Prevent duplicate entry logs within 1-minute cooldown
                                - Calculate total travel distance using the Haversine formula
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ermiş Market Tech")
                                .email("tech@ermis_market.com.tr"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")
                ));
    }
}
