package com.migros.couriertracking.service;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.dto.StoreEntryResponse;
import java.util.List;

/**
 * Service interface for courier tracking operations.
 */
public interface CourierService {

    /**
     * Processes a courier location update.
     *
     * @param request The courier location request data
     */
    void processLocation(CourierLocationRequest request);

    /**
     * Gets the total travel distance for a courier.
     *
     * @param courierId The unique ID of the courier
     * @return Total distance response containing metrics in meters and kilometers
     */
    CourierTotalDistanceResponse getTotalDistance(String courierId);

    /**
     * Gets all logged store entries for a courier.
     *
     * @param courierId The unique ID of the courier
     * @return List of store entry logs
     */
    List<StoreEntryResponse> getStoreEntries(String courierId);
}
