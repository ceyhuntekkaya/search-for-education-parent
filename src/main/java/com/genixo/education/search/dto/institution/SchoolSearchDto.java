package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSearchDto {
    private String searchTerm;
    private List<Long> institutionTypeIds;
    private Integer minAge;
    private Integer maxAge;
    private Double minFee;
    private Double maxFee;
    private String curriculumType;
    private String languageOfInstruction;

    // Location filters
    private Long countryId;
    private Long provinceId;
    private Long districtId;
    private Long neighborhoodId;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;

    // Quality filters
    private Double minRating;
    private Boolean hasActiveCampaigns;
    private Boolean isSubscribed;

    // Property filters
    //private Map<String, Object> propertyFilters;
    private List<Long>  propertyFilters;

    // Sorting
    private String sortBy; // RATING, PRICE, DISTANCE, NAME, CREATED_DATE
    private String sortDirection;

    // Pagination
    private Integer page;
    private Integer size;


    List<InstitutionTypeListDto> institutionTypes;
}