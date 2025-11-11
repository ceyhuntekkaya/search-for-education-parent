package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceAnalyticsDto {
    private String sourceName;
    private String sourceType; // DIRECT, SOCIAL_MEDIA, EMAIL, SEARCH, etc.
    private Long views;
    private Long clicks;
    private Long applications;
    private Long conversions;
    private Double conversionRate;
    private String deviceType;
}