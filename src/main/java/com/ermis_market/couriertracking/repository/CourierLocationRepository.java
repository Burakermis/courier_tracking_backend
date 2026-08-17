package com.ermis_market.couriertracking.repository;

import com.ermis_market.couriertracking.entity.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {

    List<CourierLocation> findByCourierIdOrderByTimestampDesc(String courierId);

    long countByCourierId(String courierId);
}
