package com.ermis_market.couriertracking.repository;

import com.ermis_market.couriertracking.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    Optional<Courier> findByCourierId(String courierId);

    boolean existsByCourierId(String courierId);
}
