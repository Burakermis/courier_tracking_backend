package com.ermis_market.couriertracking.service.impl;

import com.ermis_market.couriertracking.dto.CourierLocationRequest;
import com.ermis_market.couriertracking.dto.CourierTotalDistanceResponse;
import com.ermis_market.couriertracking.dto.StoreEntryResponse;
import com.ermis_market.couriertracking.entity.Courier;
import com.ermis_market.couriertracking.entity.CourierLocation;
import com.ermis_market.couriertracking.event.CourierLocationUpdatedEvent;
import com.ermis_market.couriertracking.exception.InvalidCourierException;
import com.ermis_market.couriertracking.repository.CourierLocationRepository;
import com.ermis_market.couriertracking.repository.CourierRepository;
import com.ermis_market.couriertracking.repository.StoreEntryRepository;
import com.ermis_market.couriertracking.service.contract.CourierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final CourierLocationRepository courierLocationRepository;
    private final StoreEntryRepository storeEntryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void processLocation(CourierLocationRequest request) {
        LocalDateTime timestamp = request.getTimestamp() != null
                ? request.getTimestamp()
                : LocalDateTime.now();

        Courier courier = courierRepository.findByCourierId(request.getCourierId())
                .orElseThrow(() -> new InvalidCourierException("Courier not found with ID: " + request.getCourierId()));

        courierLocationRepository.save(CourierLocation.builder()
                .courierId(request.getCourierId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(timestamp)
                .build());

        // Publish event for observers
        CourierLocationUpdatedEvent event = new CourierLocationUpdatedEvent(this, courier, request, timestamp);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public CourierTotalDistanceResponse getTotalDistance(String courierId) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new InvalidCourierException("Courier not found: " + courierId));

        return CourierTotalDistanceResponse.builder()
                .courierId(courierId)
                .totalDistanceMeters(Math.round(courier.getTotalDistance() * 100.0) / 100.0)
                .totalDistanceKilometers(Math.round(courier.getTotalDistance() / 10.0) / 100.0)
                .build();
    }

    @Override
    public List<StoreEntryResponse> getStoreEntries(String courierId) {
        if (!courierRepository.existsByCourierId(courierId)) {
            throw new InvalidCourierException("Courier not found: " + courierId);
        }
        return storeEntryRepository.findByCourierIdOrderByEntryTimeDesc(courierId)
                .stream()
                .map(entry -> StoreEntryResponse.builder()
                        .storeName(entry.getStore().getName())
                        .storeLatitude(entry.getStore().getLatitude())
                        .storeLongitude(entry.getStore().getLongitude())
                        .entryTime(entry.getEntryTime())
                        .build())
                .collect(Collectors.toList());
    }
}
