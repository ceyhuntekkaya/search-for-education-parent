package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationHierarchyDto {
    private CountrySummaryDto country;
    private ProvinceSummaryDto province;
    private DistrictSummaryDto district;
    private NeighborhoodSummaryDto neighborhood;
}