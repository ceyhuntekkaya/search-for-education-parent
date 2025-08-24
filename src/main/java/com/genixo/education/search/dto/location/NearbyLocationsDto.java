package com.genixo.education.search.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyLocationsDto {
    private CoordinatesDto center;
    private Double radiusKm;
    private List<LocationDistanceDto> locations;
    private Integer totalCount;
}