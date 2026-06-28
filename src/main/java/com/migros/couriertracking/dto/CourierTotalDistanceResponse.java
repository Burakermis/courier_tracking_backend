package com.migros.couriertracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Courier total travel distance response")
public class CourierTotalDistanceResponse {

    @Schema(description = "Courier identifier", example = "courier-001")
    private String courierId;

    @Schema(description = "Total distance traveled in meters", example = "1523.75")
    private Double totalDistanceMeters;

    @Schema(description = "Total distance traveled in kilometers", example = "1.52")
    private Double totalDistanceKilometers;
}
