package com.genixo.education.search.entity.location;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.DistrictType;
import com.genixo.education.search.enumaration.SocioeconomicLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "districts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class District extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "district_type", nullable = false)
    private DistrictType districtType;

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

    @Column(name = "is_central")
    private Boolean isCentral = false; // Merkez ilçe mi?

    @Column(name = "is_coastal")
    private Boolean isCoastal = false; // Sahil ilçesi mi?

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // SEO fields
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Economic indicators
    @Column(name = "average_income")
    private Double averageIncome;

    @Column(name = "property_price_index")
    private Double propertyPriceIndex;

    @Column(name = "cost_of_living_index")
    private Double costOfLivingIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "socioeconomic_level")
    private SocioeconomicLevel socioeconomicLevel;

    // Transportation
    @Column(name = "has_metro_station")
    private Boolean hasMetroStation = false;

    @Column(name = "has_bus_terminal")
    private Boolean hasBusTerminal = false;

    @Column(name = "has_train_station")
    private Boolean hasTrainStation = false;

    @Column(name = "distance_to_airport_km")
    private Double distanceToAirportKm;

    @Column(name = "distance_to_city_center_km")
    private Double distanceToCityCenterKm;

    @Column(name = "public_transport_score")
    private Integer publicTransportScore; // 1-10

    @Column(name = "traffic_congestion_level")
    private String trafficCongestionLevel; // LOW, MEDIUM, HIGH

    // Education statistics
    @Column(name = "school_count")
    private Long schoolCount = 0L;

    @Column(name = "private_school_count")
    private Long privateSchoolCount = 0L;

    @Column(name = "public_school_count")
    private Long publicSchoolCount = 0L;

    @Column(name = "university_count")
    private Long universityCount = 0L;

    @Column(name = "education_quality_index")
    private Double educationQualityIndex;

    @Column(name = "literacy_rate")
    private Double literacyRate;

    // Demographics
    @Column(name = "youth_population_percentage")
    private Double youthPopulationPercentage;

    @Column(name = "elderly_population_percentage")
    private Double elderlyPopulationPercentage;

    @Column(name = "average_family_size")
    private Double averageFamilySize;

    @Column(name = "birth_rate")
    private Double birthRate;

    // Infrastructure & Services
    @Column(name = "hospital_count")
    private Integer hospitalCount = 0;

    @Column(name = "shopping_mall_count")
    private Integer shoppingMallCount = 0;

    @Column(name = "park_count")
    private Integer parkCount = 0;

    @Column(name = "cultural_center_count")
    private Integer culturalCenterCount = 0;

    @Column(name = "sports_facility_count")
    private Integer sportsFacilityCount = 0;

    @Column(name = "safety_index")
    private Double safetyIndex; // 1-10

    @Column(name = "air_quality_index")
    private Double airQualityIndex;

    @Column(name = "noise_level")
    private String noiseLevel; // LOW, MEDIUM, HIGH

    // Climate data
    @Column(name = "climate_type")
    private String climateType;

    @Column(name = "average_temperature")
    private Double averageTemperature;

    @Column(name = "annual_rainfall_mm")
    private Double annualRainfallMm;

    @Column(name = "humidity_percentage")
    private Double humidityPercentage;

    // Relationships
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Neighborhood> neighborhoods = new HashSet<>();
}