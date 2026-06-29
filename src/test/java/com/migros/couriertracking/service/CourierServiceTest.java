package com.migros.couriertracking.service;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.entity.Courier;
import com.migros.couriertracking.entity.Store;
import com.migros.couriertracking.entity.StoreEntry;
import com.migros.couriertracking.exception.InvalidCourierException;
import com.migros.couriertracking.repository.CourierLocationRepository;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.repository.StoreEntryRepository;
import com.migros.couriertracking.repository.StoreRepository;
import com.migros.couriertracking.service.impl.CourierServiceImpl;
import com.migros.couriertracking.service.strategy.DistanceStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourierService Tests")
class CourierServiceTest {

    @Mock private CourierRepository courierRepository;
    @Mock private CourierLocationRepository courierLocationRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private StoreEntryRepository storeEntryRepository;
    @Mock private DistanceStrategy distanceStrategy;

    @InjectMocks
    private CourierServiceImpl courierService;

    private Store atasehirStore;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courierService, "storeRadiusMeters", 100.0);
        ReflectionTestUtils.setField(courierService, "reentryCooldownMinutes", 1L);

        atasehirStore = Store.builder()
                .id(1L)
                .name("Ataşehir MMM Migros")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .build();
    }

    @Test
    @DisplayName("processLocation should throw InvalidCourierException when courier not found")
    void testProcessLocationThrowsExceptionWhenCourierNotFound() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .build();

        when(courierRepository.findByCourierId("courier-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courierService.processLocation(request))
                .isInstanceOf(InvalidCourierException.class)
                .hasMessageContaining("courier-001");
    }

    @Test
    @DisplayName("Store entry should be logged when courier is within 100m")
    void testStoreEntryLoggedWhenWithinRadius() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(LocalDateTime.now())
                .build();

        Courier courier = Courier.builder()
                .courierId("courier-001")
                .totalDistance(0.0)
                .build();

        when(courierRepository.findByCourierId("courier-001")).thenReturn(Optional.of(courier));
        when(courierRepository.save(any())).thenReturn(courier);
        when(courierLocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeRepository.findAll()).thenReturn(List.of(atasehirStore));
        when(distanceStrategy.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(50.0); // 50m - inside radius
        when(storeEntryRepository.findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        when(storeEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        courierService.processLocation(request);

        verify(storeEntryRepository).save(any(StoreEntry.class));
    }

    @Test
    @DisplayName("Store entry should NOT be logged when courier is outside 100m")
    void testStoreEntryNotLoggedWhenOutsideRadius() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(41.0)
                .longitude(29.2)
                .build();

        Courier courier = Courier.builder().courierId("courier-001").totalDistance(0.0).build();

        when(courierRepository.findByCourierId(any())).thenReturn(Optional.of(courier));
        when(courierRepository.save(any())).thenReturn(courier);
        when(courierLocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeRepository.findAll()).thenReturn(List.of(atasehirStore));
        when(distanceStrategy.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(500.0); // 500m - outside radius

        courierService.processLocation(request);

        verify(storeEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Re-entry should be ignored within 1 minute cooldown")
    void testReentryIgnoredWithinCooldown() {
        LocalDateTime now = LocalDateTime.now();
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(now)
                .build();

        Courier courier = Courier.builder().courierId("courier-001").totalDistance(0.0).build();
        StoreEntry recentEntry = StoreEntry.builder()
                .courierId("courier-001")
                .store(atasehirStore)
                .entryTime(now.minusSeconds(30)) // 30 seconds ago - within cooldown
                .build();

        when(courierRepository.findByCourierId(any())).thenReturn(Optional.of(courier));
        when(courierRepository.save(any())).thenReturn(courier);
        when(courierLocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeRepository.findAll()).thenReturn(List.of(atasehirStore));
        when(distanceStrategy.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(50.0);
        when(storeEntryRepository.findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(anyString(), anyLong()))
                .thenReturn(Optional.of(recentEntry));

        courierService.processLocation(request);

        verify(storeEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Re-entry should be allowed after cooldown period")
    void testReentryAllowedAfterCooldown() {
        LocalDateTime now = LocalDateTime.now();
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(now)
                .build();

        Courier courier = Courier.builder().courierId("courier-001").totalDistance(0.0).build();
        StoreEntry oldEntry = StoreEntry.builder()
                .courierId("courier-001")
                .store(atasehirStore)
                .entryTime(now.minusMinutes(5)) // 5 minutes ago - past cooldown
                .build();

        when(courierRepository.findByCourierId(any())).thenReturn(Optional.of(courier));
        when(courierRepository.save(any())).thenReturn(courier);
        when(courierLocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeRepository.findAll()).thenReturn(List.of(atasehirStore));
        when(distanceStrategy.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(50.0);
        when(storeEntryRepository.findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(anyString(), anyLong()))
                .thenReturn(Optional.of(oldEntry));
        when(storeEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        courierService.processLocation(request);

        verify(storeEntryRepository).save(any(StoreEntry.class));
    }

    @Test
    @DisplayName("getTotalDistance should throw InvalidCourierException for unknown courier")
    void testGetTotalDistanceThrowsForUnknownCourier() {
        when(courierRepository.findByCourierId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courierService.getTotalDistance("unknown"))
                .isInstanceOf(InvalidCourierException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("getTotalDistance should return correct metrics")
    void testGetTotalDistanceReturnsCorrectData() {
        Courier courier = Courier.builder()
                .courierId("courier-001")
                .totalDistance(1523.75)
                .build();
        when(courierRepository.findByCourierId("courier-001")).thenReturn(Optional.of(courier));

        CourierTotalDistanceResponse response = courierService.getTotalDistance("courier-001");

        assertThat(response.getCourierId()).isEqualTo("courier-001");
        assertThat(response.getTotalDistanceMeters()).isEqualTo(1523.75);
        assertThat(response.getTotalDistanceKilometers()).isEqualTo(1.52);
    }
}
