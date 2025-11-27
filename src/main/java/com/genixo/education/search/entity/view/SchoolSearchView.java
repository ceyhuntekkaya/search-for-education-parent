package com.genixo.education.search.entity.view;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Materialized View Entity for School Search
 * Read-only, optimized for search and filtering
 */
@Entity
@Table(name = "school_search_hybrid")
@Immutable // Sadece okuma, update/delete yok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolSearchView {

    @Id
    @Column(name = "school_id")
    private Long schoolId;

    // ==================== SCHOOL BASIC ====================
    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "school_slug")
    private String schoolSlug;

    @Column(name = "school_description", columnDefinition = "TEXT")
    private String schoolDescription;

    @Column(name = "school_logo_url")
    private String schoolLogoUrl;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "current_student_count")
    private Integer currentStudentCount;

    @Column(name = "class_size_average")
    private Integer classSizeAverage;

    @Column(name = "curriculum_type")
    private String curriculumType;

    @Column(name = "language_of_instruction")
    private String languageOfInstruction;

    @Column(name = "foreign_languages")
    private String foreignLanguages;

    // Statistics
    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "rating_average")
    private Double ratingAverage;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    // ==================== CAMPUS ====================
    @Column(name = "campus_id")
    private Long campusId;

    @Column(name = "campus_name")
    private String campusName;

    @Column(name = "campus_slug")
    private String campusSlug;

    @Column(name = "campus_email")
    private String campusEmail;

    @Column(name = "campus_phone")
    private String campusPhone;

    @Column(name = "campus_website_url")
    private String campusWebsiteUrl;

    @Column(name = "campus_latitude")
    private Double campusLatitude;

    @Column(name = "campus_longitude")
    private Double campusLongitude;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "campus_is_subscribed")
    private Boolean campusIsSubscribed;

    // ==================== BRAND ====================
    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "brand_slug")
    private String brandSlug;

    // ==================== INSTITUTION TYPE ====================
    @Column(name = "institution_type_id")
    private Long institutionTypeId;

    @Column(name = "institution_type_name")
    private String institutionTypeName;

    @Column(name = "institution_type_display_name")
    private String institutionTypeDisplayName;

    @Column(name = "institution_type_group_id")
    private Long institutionTypeGroupId;

    @Column(name = "institution_type_group_name")
    private String institutionTypeGroupName;

    @Column(name = "institution_type_group_display_name")
    private String institutionTypeGroupDisplayName;

    // ==================== LOCATION ====================
    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "district_type")
    private String districtType;

    @Column(name = "neighborhood_id")
    private Long neighborhoodId;

    @Column(name = "neighborhood_name")
    private String neighborhoodName;

    @Column(name = "neighborhood_type")
    private String neighborhoodType;

    // ==================== PRICING ====================
    @Column(name = "current_monthly_tuition")
    private BigDecimal currentMonthlyTuition;

    @Column(name = "current_annual_tuition")
    private BigDecimal currentAnnualTuition;

    @Column(name = "current_registration_fee")
    private BigDecimal currentRegistrationFee;

    @Column(name = "pricing_currency")
    private String pricingCurrency;

    // ==================== PROPERTIES (JSONB) ====================
    @Type(JsonBinaryType.class)
    @Column(name = "properties", columnDefinition = "jsonb")
    private String properties; // JSONB - JSON array string

    @Column(name = "property_names_text", columnDefinition = "TEXT")
    private String propertyNamesText;

    @Column(name = "property_count")
    private Integer propertyCount;

    // ==================== CAMPAIGN ====================
    @Column(name = "has_active_campaign")
    private Boolean hasActiveCampaign;

    // ==================== JSON DETAILS ====================
    @Type(JsonBinaryType.class)
    @Column(name = "campus_details", columnDefinition = "json")
    private String campusDetails;

    @Type(JsonBinaryType.class)
    @Column(name = "brand_details", columnDefinition = "json")
    private String brandDetails;

    @Type(JsonBinaryType.class)
    @Column(name = "district_stats", columnDefinition = "json")
    private String districtStats;

    @Type(JsonBinaryType.class)
    @Column(name = "neighborhood_stats", columnDefinition = "json")
    private String neighborhoodStats;

    @Type(JsonBinaryType.class)
    @Column(name = "pricings", columnDefinition = "json")
    private String pricings;

    @Type(JsonBinaryType.class)
    @Column(name = "campaigns", columnDefinition = "json")
    private String campaigns;

    @Type(JsonBinaryType.class)
    @Column(name = "custom_fees", columnDefinition = "json")
    private String customFees;

    // ==================== SEARCH TEXT ====================
    @Column(name = "search_text", columnDefinition = "TEXT")
    private String searchText;
}