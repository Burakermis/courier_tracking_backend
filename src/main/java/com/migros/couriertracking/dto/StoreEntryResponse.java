package com.migros.couriertracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Store entry log for a courier")
public class StoreEntryResponse {

    @Schema(description = "Store name", example = "Ataşehir MMM Migros")
    private String storeName;

    @Schema(description = "Store latitude", example = "40.9923307")
    private Double storeLatitude;

    @Schema(description = "Store longitude", example = "29.1244229")
    private Double storeLongitude;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Time when courier entered the store's radius", example = "2024-01-15T10:30:00")
    private LocalDateTime entryTime;
}
