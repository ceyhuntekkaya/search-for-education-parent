package com.genixo.education.search.dto.location;

import com.genixo.education.search.enumaration.DevelopmentLevel;
import com.genixo.education.search.enumaration.HousingType;
import com.genixo.education.search.enumaration.IncomeLevel;
import com.genixo.education.search.enumaration.NeighborhoodType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborhoodDto {
    private Long id;
    private String name;
    private String nameEn;
    private String code;
    private NeighborhoodType neighborhoodType;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Long population;
    private Double areaKm2;
    private Integer elevationM;
    private Double densityPerKm2;
    private Integer sortOrder;
    private String slug;
    private String description;

    // Characteristics
    private HousingType housingType;
    private DevelopmentLevel developmentLevel;
    private Boolean isGatedCommunity;
    private Boolean isHistorical;
    private Boolean isCommercialCenter;
    private Boolean isResidential;
    private Boolean isIndustrial;

    // Economic data
    private Double averageRentPrice;
    private Double averagePropertyPrice;
    private Double propertyPricePerM2;
    private IncomeLevel incomeLevel;

    // Transportation accessibility
    private Integer metroAccessibilityMinutes;
    private Integer busAccessibilityMinutes;
    private Integer mainRoadAccessibilityMinutes;
    private Integer highwayAccessibilityMinutes;
    private Integer publicTransportFrequency;
    private String parkingAvailability;
    private Integer walkabilityScore;

    // Infrastructure & Amenities
    private Boolean hasMetroStation;
    private Boolean hasHospital;
    private Boolean hasShoppingCenter;
    private Boolean hasPark;
    private Boolean hasLibrary;
    private Boolean hasSportsFacility;
    private Boolean hasCulturalCenter;
    private Boolean hasKindergarten;
    private Integer restaurantCount;
    private Integer cafeCount;
    private Integer bankCount;
    private Integer pharmacyCount;
    private Integer supermarketCount;

    // Education
    private Long schoolCount;
    private Long privateSchoolCount;
    private Long publicSchoolCount;
    private Long preschoolCount;
    private Integer educationAccessibilityScore;
    private Double schoolQualityIndex;

    // Quality of life
    private Integer safetyScore;
    private String noiseLevel;
    private Integer airQualityScore;
    private Double greenSpacePercentage;
    private Integer cleanlinessScore;
    private Integer socialLifeScore;

    // Demographics
    private Double averageAge;
    private Double familyWithChildrenPercentage;
    private Double youngProfessionalPercentage;
    private Double elderlyPercentage;
    private Double studentPercentage;

    // Real estate trends
    private String propertyDemandLevel;
    private String developmentPotential;
    private Integer investmentAttractiveness;

    // Parent preferences
    private Integer schoolPreferenceScore;
    private Integer commuteToBusinessDistrictsMinutes;
    private Integer familyFriendlinessScore;

    // Relationships
    private DistrictSummaryDto district;
    private Boolean isActive;
    private LocalDateTime createdAt;
}