package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSearchResultDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String coverImageUrl;
    private String description;
    private String institutionTypeName;
    private String institutionTypeIcon;
    private String institutionTypeColor;

    // Basic info
    private Integer minAge;
    private Integer maxAge;
    private String ageRange;
    private Double monthlyFee;
    private String formattedPrice;
    private Double ratingAverage;
    private Long ratingCount;

    // Location
    private String campusName;
    private String address;
    private String district;
    private String city;
    private Double distanceKm;

    // Highlights
    private List<String> highlights; // Matching search terms
    private List<InstitutionPropertyValueDto> cardProperties;
    private List<CampaignSummaryDtoSil> activeCampaigns;

    // Status
    private Boolean hasActiveCampaigns;
    private Boolean isSubscribed;
    private Boolean isFavorite;

    private InstitutionTypeListDto properties;

}
