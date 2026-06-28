package com.migros.couriertracking.service.impl;

import com.migros.couriertracking.entity.Store;
import com.migros.couriertracking.repository.StoreRepository;
import com.migros.couriertracking.service.StoreService;
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
