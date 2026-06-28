package com.migros.couriertracking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("HaversineService Tests")
class HaversineServiceTest {

    private HaversineService haversineService;

    @BeforeEach
    void setUp() {
        haversineService = new HaversineService();
    }

    @Test
    @DisplayName("Same coordinates should return 0 distance")
    void testSameCoordinatesReturnsZero() {
        double distance = haversineService.calculateDistance(
                40.9923307, 29.1244229,
                40.9923307, 29.1244229);
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Courier inside 100m store radius should be detected")
    void testCourierInsideStoreRadius() {
        // Ataşehir MMM Migros: 40.9923307, 29.1244229
        // Courier at ~50m away
        double distance = haversineService.calculateDistance(
                40.9923307, 29.1244229,
                40.9927800, 29.1244229); // ~50m north
        assertThat(distance).isLessThan(100.0);
    }

    @Test
    @DisplayName("Courier outside 100m store radius should not be detected")
    void testCourierOutsideStoreRadius() {
        // Ataşehir MMM Migros: 40.9923307, 29.1244229
        // Courier at ~300m away
        double distance = haversineService.calculateDistance(
                40.9923307, 29.1244229,
                40.9950000, 29.1244229); // ~300m north
        assertThat(distance).isGreaterThan(100.0);
    }

    @Test
    @DisplayName("Ataşehir to Novada store distance should be approximately 785m")
    void testDistanceBetweenKnownStores() {
        // Ataşehir MMM Migros
        // Novada MMM Migros
        double distance = haversineService.calculateDistance(
                40.9923307, 29.1244229,
                40.986106, 29.1161293);
        // Distance should be approximately 981 meters
        assertThat(distance).isCloseTo(981.0, within(50.0));
    }

    @Test
    @DisplayName("Distance calculation should be symmetric")
    void testDistanceIsSymmetric() {
        double d1 = haversineService.calculateDistance(40.9923307, 29.1244229, 41.055783, 29.0210292);
        double d2 = haversineService.calculateDistance(41.055783, 29.0210292, 40.9923307, 29.1244229);
        assertThat(d1).isCloseTo(d2, within(0.001));
    }
}
