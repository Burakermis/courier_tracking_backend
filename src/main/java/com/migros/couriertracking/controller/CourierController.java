package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.ApiResponse;
import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.CourierTotalDistanceResponse;
import com.migros.couriertracking.dto.StoreEntryResponse;
import com.migros.couriertracking.service.contract.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
@Tag(name = "Courier", description = "Courier location tracking operations")
public class CourierController {

    private final CourierService courierService;

    @PostMapping("/locations")
    @Operation(
        summary = "Update courier location",
        description = "Receives a courier's real-time location. Automatically tracks total distance traveled " +
                      "and logs entries when the courier comes within 100m of a Migros store."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Location recorded successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @Valid @RequestBody CourierLocationRequest request) {
        courierService.processLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location recorded successfully", null));
    }

    @GetMapping("/{courierId}/total-distance")
    @Operation(
        summary = "Get total travel distance",
        description = "Returns the total distance a courier has traveled, calculated using the Haversine formula."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Distance retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Courier not found")
    })
    public ResponseEntity<ApiResponse<CourierTotalDistanceResponse>> getTotalDistance(
            @Parameter(description = "Courier ID", example = "courier-001")
            @PathVariable String courierId) {
        CourierTotalDistanceResponse response = courierService.getTotalDistance(courierId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{courierId}/store-entries")
    @Operation(
        summary = "Get store entry logs",
        description = "Returns all logged Migros store entries for a courier, sorted by most recent first."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store entries retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Courier not found")
    })
    public ResponseEntity<ApiResponse<List<StoreEntryResponse>>> getStoreEntries(
            @Parameter(description = "Courier ID", example = "courier-001")
            @PathVariable String courierId) {
        List<StoreEntryResponse> entries = courierService.getStoreEntries(courierId);
        return ResponseEntity.ok(ApiResponse.success(entries));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure("Validation failed", null));
    }
}
