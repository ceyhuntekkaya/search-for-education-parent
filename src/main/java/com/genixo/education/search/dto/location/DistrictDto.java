package com.genixo.education.search.dto.location;

import com.genixo.education.search.enumaration.DistrictType;
import com.genixo.education.search.enumaration.SocioeconomicLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictDto {
    private Long id;
    private String name;
    private String nameEn;
    private String code;
    private DistrictType districtType;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Long population;
    private Double areaKm2;
    private Integer elevationM;
    private Double densityPerKm2;
    private Boolean isCentral;
    private Boolean isCoastal;
    private Integer sortOrder;
    private String slug;
    private String description;

    // Economic indicators
    private Double averageIncome;
    private Double propertyPriceIndex;
    private Double costOfLivingIndex;
    private SocioeconomicLevel socioeconomicLevel;

    // Transportation
    private Boolean hasMetroStation;
    private Boolean hasBusTerminal;
    private Boolean hasTrainStation;
    private Double distanceToAirportKm;
    private Double distanceToCityCenterKm;
    private Integer publicTransportScore;
    private String trafficCongestionLevel;

    // Education statistics
    private Long schoolCount;
    private Long privateSchoolCount;
    private Long publicSchoolCount;
    private Long universityCount;
    private Double educationQualityIndex;
    private Double literacyRate;

    // Demographics
    private Double youthPopulationPercentage;
    private Double elderlyPopulationPercentage;
    private Double averageFamilySize;
    private Double birthRate;

    // Infrastructure & Services
    private Integer hospitalCount;
    private Integer shoppingMallCount;
    private Integer parkCount;
    private Integer culturalCenterCount;
    private Integer sportsFacilityCount;
    private Double safetyIndex;
    private Double airQualityIndex;
    private String noiseLevel;

    // Climate
    private String climateType;
    private Double averageTemperature;
    private Double annualRainfallMm;
    private Double humidityPercentage;

    // Relationships
    private ProvinceSummaryDto province;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
