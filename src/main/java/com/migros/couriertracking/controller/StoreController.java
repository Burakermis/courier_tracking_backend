package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.ApiResponse;
import com.migros.couriertracking.entity.Store;
import com.migros.couriertracking.service.contract.StoreService;
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
@Tag(name = "Store", description = "Migros store management operations")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @Operation(
        summary = "List all Migros stores",
        description = "Returns all Migros store locations loaded from stores.json at startup."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stores retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(stores));
    }
}
