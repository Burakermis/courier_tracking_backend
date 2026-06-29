package com.migros.couriertracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.entity.Courier;
import com.migros.couriertracking.repository.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Courier Controller Integration Tests")
class CourierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourierRepository courierRepository;

    @BeforeEach
    void setUp() {
        if (!courierRepository.existsByCourierId("test-courier-1")) {
            courierRepository.save(Courier.builder().courierId("test-courier-1").totalDistance(0.0).build());
        }
        if (!courierRepository.existsByCourierId("test-courier-2")) {
            courierRepository.save(Courier.builder().courierId("test-courier-2").totalDistance(0.0).build());
        }
        if (!courierRepository.existsByCourierId("test-courier-3")) {
            courierRepository.save(Courier.builder().courierId("test-courier-3").totalDistance(0.0).build());
        }
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/couriers/locations - should return 201 for valid request")
    void testPostLocationReturns201() throws Exception {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("test-courier-1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/couriers/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/couriers/locations - should return 400 for missing courierId")
    void testPostLocationReturns400ForMissingCourierId() throws Exception {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .latitude(40.9923307)
                .longitude(29.1244229)
                .build();

        mockMvc.perform(post("/api/couriers/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/couriers/{id}/total-distance - should return distance for existing courier")
    void testGetTotalDistanceForExistingCourier() throws Exception {
        // First create the courier by sending a location
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("test-courier-2")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .build();
        mockMvc.perform(post("/api/couriers/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/couriers/test-courier-2/total-distance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courierId").value("test-courier-2"))
                .andExpect(jsonPath("$.data.totalDistanceMeters").exists());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/couriers/{id}/total-distance - should return 404 for unknown courier")
    void testGetTotalDistanceReturns404ForUnknownCourier() throws Exception {
        mockMvc.perform(get("/api/couriers/nonexistent-courier/total-distance"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/stores - should return all 5 Migros stores")
    void testGetAllStoresReturns5Stores() throws Exception {
        mockMvc.perform(get("/api/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/couriers/locations - store entry should be logged when near Ataşehir Migros")
    void testStoreEntryLoggedWhenNearStore() throws Exception {
        // Send location exactly at Ataşehir MMM Migros
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("test-courier-3")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .build();

        mockMvc.perform(post("/api/couriers/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Check store entries
        mockMvc.perform(get("/api/couriers/test-courier-3/store-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].storeName").value("Ataşehir MMM Migros"));
    }
}
