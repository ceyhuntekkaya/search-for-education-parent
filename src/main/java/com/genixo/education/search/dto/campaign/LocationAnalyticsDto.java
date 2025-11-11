package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationAnalyticsDto {
    private String locationName;
    private String locationType; // PROVINCE, DISTRICT, NEIGHBORHOOD
    private Long views;
    private Long applications;
    private Long conversions;
    private Double conversionRate;
    private Integer schoolCount;
}