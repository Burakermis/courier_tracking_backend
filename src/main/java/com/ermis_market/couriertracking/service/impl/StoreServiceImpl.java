package com.ermis_market.couriertracking.service.impl;

import com.ermis_market.couriertracking.entity.Store;
import com.ermis_market.couriertracking.repository.StoreRepository;
import com.ermis_market.couriertracking.service.contract.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
}
