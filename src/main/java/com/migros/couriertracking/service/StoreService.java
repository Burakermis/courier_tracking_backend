package com.migros.couriertracking.service;

import com.migros.couriertracking.entity.Store;
import java.util.List;

/**
 * Service interface for store management operations.
 */
public interface StoreService {

    /**
     * Retrieves all Migros stores.
     *
     * @return List of all stores
     */
    List<Store> getAllStores();
}
