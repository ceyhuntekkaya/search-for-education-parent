package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDistanceDto {
    private LocationSuggestionDto location;
    private Double distanceKm;
    private Integer estimatedTravelTimeMinutes;
    private String transportationMethod;
}