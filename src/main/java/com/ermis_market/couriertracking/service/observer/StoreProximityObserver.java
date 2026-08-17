package com.ermis_market.couriertracking.service.observer;

import com.ermis_market.couriertracking.entity.Store;
import com.ermis_market.couriertracking.entity.StoreEntry;
import com.ermis_market.couriertracking.event.CourierLocationUpdatedEvent;
import com.ermis_market.couriertracking.repository.StoreEntryRepository;
import com.ermis_market.couriertracking.repository.StoreRepository;
import com.ermis_market.couriertracking.service.strategy.DistanceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreProximityObserver {

    private final StoreRepository storeRepository;
    private final StoreEntryRepository storeEntryRepository;
    private final DistanceStrategy distanceStrategy;

    @Value("${courier.tracking.store-radius-meters:100.0}")
    private double storeRadiusMeters;

    @Value("${courier.tracking.reentry-cooldown-minutes:1}")
    private long reentryCooldownMinutes;

    @EventListener
    public void onLocationUpdated(CourierLocationUpdatedEvent event) {
        String courierId = event.getRequest().getCourierId();
        double lat = event.getRequest().getLatitude();
        double lng = event.getRequest().getLongitude();
        LocalDateTime timestamp = event.getEventTime();

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
}
