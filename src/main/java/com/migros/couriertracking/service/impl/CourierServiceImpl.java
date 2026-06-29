package com.migros.couriertracking.service.impl;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.dto.StoreEntryResponse;
import com.migros.couriertracking.entity.Courier;
import com.migros.couriertracking.entity.CourierLocation;
import com.migros.couriertracking.entity.Store;
import com.migros.couriertracking.entity.StoreEntry;
import com.migros.couriertracking.exception.CourierNotFoundException;
import com.migros.couriertracking.repository.CourierLocationRepository;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.repository.StoreEntryRepository;
import com.migros.couriertracking.repository.StoreRepository;
import com.migros.couriertracking.service.CourierService;
import com.migros.couriertracking.service.strategy.DistanceStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final StoreRepository storeRepository;
    private final StoreEntryRepository storeEntryRepository;
    private final DistanceStrategy distanceStrategy;

    @Value("${courier.tracking.store-radius-meters:100.0}")
    private double storeRadiusMeters;

    @Value("${courier.tracking.reentry-cooldown-minutes:1}")
    private long reentryCooldownMinutes;

    @Override
    @Transactional
    public void processLocation(CourierLocationRequest request) {
        LocalDateTime timestamp = request.getTimestamp() != null
                ? request.getTimestamp()
                : LocalDateTime.now();

        // Step 1: Get or create courier
        Courier courier = courierRepository.findByCourierId(request.getCourierId())
                .orElseGet(() -> {
                    log.info("Creating new courier: {}", request.getCourierId());
                    return courierRepository.save(Courier.builder()
                            .courierId(request.getCourierId())
                            .totalDistance(0.0)
                            .build());
                });

        // Step 2: Save location log
        courierLocationRepository.save(CourierLocation.builder()
                .courierId(request.getCourierId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(timestamp)
                .build());

        // Step 3: Calculate and accumulate travel distance
        if (courier.getLastLatitude() != null && courier.getLastLongitude() != null) {
            double distance = distanceStrategy.calculateDistance(
                    courier.getLastLatitude(), courier.getLastLongitude(),
                    request.getLatitude(), request.getLongitude());
            courier.setTotalDistance(courier.getTotalDistance() + distance);
            log.debug("Courier {} traveled {} meters (total: {} m)",
                    request.getCourierId(), String.format("%.2f", distance),
                    String.format("%.2f", courier.getTotalDistance()));
        }

        // Step 4: Update last known position
        courier.setLastLatitude(request.getLatitude());
        courier.setLastLongitude(request.getLongitude());
        courierRepository.save(courier);

        // Step 5: Check proximity to all stores
        checkStoreProximity(request.getCourierId(), request.getLatitude(), request.getLongitude(), timestamp);
    }

    private void checkStoreProximity(String courierId, double lat, double lng, LocalDateTime timestamp) {
        List<Store> stores = storeRepository.findAll();

        for (Store store : stores) {
            double distance = distanceStrategy.calculateDistance(
                    lat, lng, store.getLatitude(), store.getLongitude());

            if (distance <= storeRadiusMeters) {
                LocalDateTime cooldownThreshold = timestamp.minusMinutes(reentryCooldownMinutes);

                boolean recentEntry = storeEntryRepository
                        .findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(courierId, store.getId())
                        .map(entry -> entry.getEntryTime().isAfter(cooldownThreshold))
                        .orElse(false);

                if (!recentEntry) {
                    storeEntryRepository.save(StoreEntry.builder()
                            .courierId(courierId)
                            .store(store)
                            .entryTime(timestamp)
                            .build());
                    log.info("[STORE ENTRY] Courier '{}' entered '{}' at {} (distance: {:.2f}m)",
                            courierId, store.getName(), timestamp, distance);
                } else {
                    log.debug("[COOLDOWN] Courier '{}' near '{}' but within cooldown period",
                            courierId, store.getName());
                }
            }
        }
    }

    @Override
    public CourierTotalDistanceResponse getTotalDistance(String courierId) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new CourierNotFoundException("Courier not found: " + courierId));

        return CourierTotalDistanceResponse.builder()
                .courierId(courierId)
                .totalDistanceMeters(Math.round(courier.getTotalDistance() * 100.0) / 100.0)
                .totalDistanceKilometers(Math.round(courier.getTotalDistance() / 10.0) / 100.0)
                .build();
    }

    @Override
    public List<StoreEntryResponse> getStoreEntries(String courierId) {
        if (!courierRepository.existsByCourierId(courierId)) {
            throw new CourierNotFoundException("Courier not found: " + courierId);
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
