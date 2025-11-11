package com.genixo.education.search.entity.location;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.DevelopmentLevel;
import com.genixo.education.search.enumaration.HousingType;
import com.genixo.education.search.enumaration.IncomeLevel;
import com.genixo.education.search.enumaration.NeighborhoodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "neighborhoods")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Neighborhood extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "neighborhood_type", nullable = false)
    private NeighborhoodType neighborhoodType;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "population")
    private Long population;

    @Column(name = "area_km2")
    private Double areaKm2;

    @Column(name = "elevation_m")
    private Integer elevationM;

    @Column(name = "density_per_km2")
    private Double densityPerKm2;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // SEO fields
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Neighborhood characteristics
    @Enumerated(EnumType.STRING)
    @Column(name = "housing_type")
    private HousingType housingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "development_level")
    private DevelopmentLevel developmentLevel;

    @Column(name = "is_gated_community")
    private Boolean isGatedCommunity = false;

    @Column(name = "is_historical")
    private Boolean isHistorical = false;

    @Column(name = "is_commercial_center")
    private Boolean isCommercialCenter = false;

    @Column(name = "is_residential")
    private Boolean isResidential = true;

    @Column(name = "is_industrial")
    private Boolean isIndustrial = false;

    // Economic data
    @Column(name = "average_rent_price")
    private Double averageRentPrice;

    @Column(name = "average_property_price")
    private Double averagePropertyPrice;

    @Column(name = "property_price_per_m2")
    private Double propertyPricePerM2;

    @Enumerated(EnumType.STRING)
    @Column(name = "income_level")
    private IncomeLevel incomeLevel;

    // Transportation accessibility
    @Column(name = "metro_accessibility_minutes")
    private Integer metroAccessibilityMinutes;

    @Column(name = "bus_accessibility_minutes")
    private Integer busAccessibilityMinutes;

    @Column(name = "main_road_accessibility_minutes")
    private Integer mainRoadAccessibilityMinutes;

    @Column(name = "highway_accessibility_minutes")
    private Integer highwayAccessibilityMinutes;

    @Column(name = "public_transport_frequency")
    private Integer publicTransportFrequency; // per hour

    @Column(name = "parking_availability")
    private String parkingAvailability; // ABUNDANT, MODERATE, SCARCE

    @Column(name = "walkability_score")
    private Integer walkabilityScore; // 1-10

    // Infrastructure & Amenities
    @Column(name = "has_metro_station")
    private Boolean hasMetroStation = false;

    @Column(name = "has_hospital")
    private Boolean hasHospital = false;

    @Column(name = "has_shopping_center")
    private Boolean hasShoppingCenter = false;

    @Column(name = "has_park")
    private Boolean hasPark = false;

    @Column(name = "has_library")
    private Boolean hasLibrary = false;

    @Column(name = "has_sports_facility")
    private Boolean hasSportsFacility = false;

    @Column(name = "has_cultural_center")
    private Boolean hasCulturalCenter = false;

    @Column(name = "has_kindergarten")
    private Boolean hasKindergarten = false;

    @Column(name = "restaurant_count")
    private Integer restaurantCount = 0;

    @Column(name = "cafe_count")
    private Integer cafeCount = 0;

    @Column(name = "bank_count")
    private Integer bankCount = 0;

    @Column(name = "pharmacy_count")
    private Integer pharmacyCount = 0;

    @Column(name = "supermarket_count")
    private Integer supermarketCount = 0;

    // Education
    @Column(name = "school_count")
    private Long schoolCount = 0L;

    @Column(name = "private_school_count")
    private Long privateSchoolCount = 0L;

    @Column(name = "public_school_count")
    private Long publicSchoolCount = 0L;

    @Column(name = "preschool_count")
    private Long preschoolCount = 0L;

    @Column(name = "education_accessibility_score")
    private Integer educationAccessibilityScore; // 1-10

    @Column(name = "school_quality_index")
    private Double schoolQualityIndex;

    // Quality of life indicators
    @Column(name = "safety_score")
    private Integer safetyScore; // 1-10

    @Column(name = "noise_level")
    private String noiseLevel; // LOW, MEDIUM, HIGH

    @Column(name = "air_quality_score")
    private Integer airQualityScore; // 1-10

    @Column(name = "green_space_percentage")
    private Double greenSpacePercentage;

    @Column(name = "cleanliness_score")
    private Integer cleanlinessScore; // 1-10

    @Column(name = "social_life_score")
    private Integer socialLifeScore; // 1-10

    // Demographics
    @Column(name = "average_age")
    private Double averageAge;

    @Column(name = "family_with_children_percentage")
    private Double familyWithChildrenPercentage;

    @Column(name = "young_professional_percentage")
    private Double youngProfessionalPercentage;

    @Column(name = "elderly_percentage")
    private Double elderlyPercentage;

    @Column(name = "student_percentage")
    private Double studentPercentage;

    // Real estate trends
    @Column(name = "property_demand_level")
    private String propertyDemandLevel; // LOW, MEDIUM, HIGH

    @Column(name = "development_potential")
    private String developmentPotential; // LOW, MEDIUM, HIGH

    @Column(name = "investment_attractiveness")
    private Integer investmentAttractiveness; // 1-10

    // Parent preferences for schools
    @Column(name = "school_preference_score")
    private Integer schoolPreferenceScore; // 1-10

    @Column(name = "commute_to_business_districts_minutes")
    private Integer commuteToBusinessDistrictsMinutes;

    @Column(name = "family_friendliness_score")
    private Integer familyFriendlinessScore; // 1-10
}
