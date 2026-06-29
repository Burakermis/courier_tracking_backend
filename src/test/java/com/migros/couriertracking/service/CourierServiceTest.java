package com.migros.couriertracking.service;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.entity.Courier;
import com.migros.couriertracking.entity.Store;
import com.migros.couriertracking.event.CourierLocationUpdatedEvent;
import com.migros.couriertracking.exception.InvalidCourierException;
import com.migros.couriertracking.repository.CourierLocationRepository;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.repository.StoreEntryRepository;
import com.migros.couriertracking.service.impl.CourierServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourierService Tests")
class CourierServiceTest {

    @Mock private CourierRepository courierRepository;
    @Mock private CourierLocationRepository courierLocationRepository;
    @Mock private StoreEntryRepository storeEntryRepository;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CourierServiceImpl courierService;

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
    @DisplayName("processLocation should publish CourierLocationUpdatedEvent")
    void testProcessLocationPublishesEvent() {
        LocalDateTime now = LocalDateTime.now();
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-001")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(now)
                .build();

        Courier courier = Courier.builder().courierId("courier-001").totalDistance(0.0).build();

        when(courierRepository.findByCourierId("courier-001")).thenReturn(Optional.of(courier));
        when(courierLocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        courierService.processLocation(request);

        ArgumentCaptor<CourierLocationUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(CourierLocationUpdatedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        CourierLocationUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getCourier()).isEqualTo(courier);
        assertThat(publishedEvent.getRequest()).isEqualTo(request);
        assertThat(publishedEvent.getEventTime()).isEqualTo(now);
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
