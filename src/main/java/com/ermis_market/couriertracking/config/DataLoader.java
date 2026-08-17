package com.ermis_market.couriertracking.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ermis_market.couriertracking.entity.Store;
import com.ermis_market.couriertracking.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (storeRepository.count() > 0) {
            log.info("Stores already loaded, skipping data initialization.");
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("stores.json");
            InputStream inputStream = resource.getInputStream();
            List<Map<String, Object>> storeData = objectMapper.readValue(
                    inputStream, new TypeReference<>() {});

            for (Map<String, Object> data : storeData) {
                Store store = Store.builder()
                        .name((String) data.get("name"))
                        .latitude(((Number) data.get("lat")).doubleValue())
                        .longitude(((Number) data.get("lng")).doubleValue())
                        .build();
                storeRepository.save(store);
            }

            log.info("Successfully loaded {} ermis market stores from stores.json", storeData.size());
        } catch (Exception e) {
            log.error("Failed to load stores from stores.json: {}", e.getMessage(), e);
            throw e;
        }
    }
}
