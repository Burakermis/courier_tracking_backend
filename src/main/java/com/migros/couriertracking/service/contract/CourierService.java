package com.migros.couriertracking.service.contract;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.dto.StoreEntryResponse;
import java.util.List;

public interface CourierService {

    void processLocation(CourierLocationRequest request);

    CourierTotalDistanceResponse getTotalDistance(String courierId);

    List<StoreEntryResponse> getStoreEntries(String courierId);
}
