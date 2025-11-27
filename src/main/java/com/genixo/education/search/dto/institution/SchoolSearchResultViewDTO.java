package com.genixo.education.search.dto.institution;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Arama sonucu - Liste görünümü için
 */
@Data
public class SchoolSearchResultViewDTO {

    // Basic Info
    private Long schoolId;
    private String schoolName;
    private String schoolSlug;
    private String schoolDescription;
    private String schoolLogoUrl;

    // Location
    private String provinceName;
    private String districtName;
    private String neighborhoodName;

    // Campus
    private String campusName;
    private Double campusLatitude;
    private Double campusLongitude;

    // Institution Type
    private String institutionTypeDisplayName;

    // Pricing
    private BigDecimal currentMonthlyTuition;
    private BigDecimal currentAnnualTuition;
    private BigDecimal currentRegistrationFee;
    private String pricingCurrency;

    // Statistics
    private Double ratingAverage;
    private Long ratingCount;
    private Long viewCount;

    // Properties
    private String propertyNamesText; // "Kütüphane, Oyun Alanı, Laboratuvar"
    private Integer propertyCount;
    private String properties; // JSON string - ihtiyaç olursa frontend parse eder

    // Campaign
    private Boolean hasActiveCampaign;

    // JSON Details (optional - detay sayfası için)
    private String campusDetails;
    private String districtStats;
    private String neighborhoodStats;
    private String pricings;
    private String campaigns;
}
