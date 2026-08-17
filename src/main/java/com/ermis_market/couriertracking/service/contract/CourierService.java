package com.ermis_market.couriertracking.service.contract;

import com.ermis_market.couriertracking.dto.CourierLocationRequest;
import com.ermis_market.couriertracking.dto.CourierTotalDistanceResponse;
import com.ermis_market.couriertracking.dto.StoreEntryResponse;
import java.util.List;

public interface CourierService {

    void processLocation(CourierLocationRequest request);

    CourierTotalDistanceResponse getTotalDistance(String courierId);

    List<StoreEntryResponse> getStoreEntries(String courierId);
}
