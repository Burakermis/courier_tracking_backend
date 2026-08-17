package com.ermis_market.couriertracking.controller;

import com.ermis_market.couriertracking.dto.ApiResponse;
import com.ermis_market.couriertracking.entity.Store;
import com.ermis_market.couriertracking.service.contract.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "Ermiş Market store management operations")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @Operation(
        summary = "List all Ermiş Market stores",
        description = "Returns all Ermiş Market store locations loaded from stores.json at startup."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stores retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(stores));
    }
}
