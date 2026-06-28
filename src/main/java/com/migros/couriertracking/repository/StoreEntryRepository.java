package com.migros.couriertracking.repository;

import com.migros.couriertracking.entity.StoreEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreEntryRepository extends JpaRepository<StoreEntry, Long> {

    List<StoreEntry> findByCourierIdOrderByEntryTimeDesc(String courierId);

    Optional<StoreEntry> findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(String courierId, Long storeId);
}
