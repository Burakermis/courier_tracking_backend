package com.ermis_market.couriertracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Courier location update request")
public class CourierLocationRequest {

    @NotBlank(message = "Courier ID is required")
    @Schema(description = "Unique courier identifier", example = "courier-001")
    private String courierId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    @Schema(description = "Latitude coordinate", example = "40.9923307")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @Schema(description = "Longitude coordinate", example = "29.1244229")
    private Double longitude;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp of the location update (defaults to now if not provided)", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
}
