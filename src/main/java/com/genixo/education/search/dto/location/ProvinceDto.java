package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceDto {
    private Long id;
    private String name;
    private String nameEn;
    private String code;
    private String plateCode;
    private String region;
    private String areaCode;
    private String postalCodePrefix;
    private Double latitude;
    private Double longitude;
    private Long population;
    private Double areaKm2;
    private Integer elevationM;
    private String timeZone;
    private Boolean isMetropolitan;
    private Integer sortOrder;
    private String slug;
    private String description;

    // Economic data
    private Double gdpPerCapita;
    private Double unemploymentRate;
    private Double educationIndex;

    // Infrastructure
    private Boolean hasAirport;
    private Boolean hasUniversity;
    private Boolean hasMetro;
    private String trafficDensity;

    // Education statistics
    private Long schoolCount;
    private Long studentCount;
    private Long teacherCount;
    private Double literacyRate;

    // Relationships
    private CountrySummaryDto country;
    private Boolean isActive;
    private LocalDateTime createdAt;
}