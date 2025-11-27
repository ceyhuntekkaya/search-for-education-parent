package com.genixo.education.search.dto.institution;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Arama filtresi - Tüm alanlar opsiyonel
 */
@Data
public class SchoolSearchFilterDTO {

    // Location filters
    private String provinceName;
    private String districtName;
    private String neighborhoodName;

    // Institution type
    private Long institutionTypeId;

    // Price filters
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Property filters (dinamik)
    private List<PropertyFilter> propertyFilters;

    // Campaign
    private Boolean hasActiveCampaign;

    // Sorting
    private String sortBy; // "rating", "price_asc", "price_desc", "newest"

    // Search keyword
    private String keyword;

    @Data
    public static class PropertyFilter {
        private String name;        // property name (örn: "has_library")
        private Boolean booleanValue;
        private Double numberValue;
        private String textValue;
    }
}