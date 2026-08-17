package com.ermis_market.couriertracking.repository;

import com.ermis_market.couriertracking.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByName(String name);
}
