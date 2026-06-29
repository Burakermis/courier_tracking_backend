package com.migros.couriertracking.service.observer;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.entity.Courier;
import com.migros.couriertracking.event.CourierLocationUpdatedEvent;
import com.migros.couriertracking.repository.CourierRepository;
import com.migros.couriertracking.service.strategy.DistanceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistanceAccumulationObserver {

    private final DistanceStrategy distanceStrategy;
    private final CourierRepository courierRepository;

    @EventListener
    public void onLocationUpdated(CourierLocationUpdatedEvent event) {
        Courier courier = event.getCourier();
        CourierLocationRequest request = event.getRequest();

        if (courier.getLastLatitude() != null && courier.getLastLongitude() != null) {
            double distance = distanceStrategy.calculateDistance(
                    courier.getLastLatitude(), courier.getLastLongitude(),
                    request.getLatitude(), request.getLongitude());
            courier.setTotalDistance(courier.getTotalDistance() + distance);
            log.debug("Courier {} traveled {} meters (total: {} m)",
                    request.getCourierId(), String.format("%.2f", distance),
                    String.format("%.2f", courier.getTotalDistance()));
        }

        courier.setLastLatitude(request.getLatitude());
        courier.setLastLongitude(request.getLongitude());
        courierRepository.save(courier);
    }
}
