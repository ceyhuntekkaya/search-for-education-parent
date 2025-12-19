package com.genixo.education.search.dto.institution;

import jakarta.persistence.Column;
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
    private String institutionTypeDisplayName;
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
    private Long campusId;
    private Boolean campusIsSubscribed;
    private String campusName;
    private String campusSlug;
    private String address;
    private String district;
    private String province;
    private String neighborhood;
    private String city;
    private Double distanceKm;
    private String fullLocation;


    // Highlights
    private List<String> highlights; // Matching search terms
    private List<InstitutionPropertyValueDto> cardProperties;
    private List<CampaignSummaryDtoSil> activeCampaigns;

    // Status
    private Boolean hasActiveCampaigns;
    private Boolean isSubscribed;
    private Boolean isFavorite;

    private InstitutionTypeListDto properties;


    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String linkedinUrl;
    private String youtubeUrl;

    private Double latitude;
    private Double longitude;
    private String ratingStars;

    private String curriculumType;
    private String languageOfInstruction;
    private String brandName;
    private String brandSlug;
    private String brandLogo;




    private Long viewCount;
    private Integer studentCapacity;
    private Integer occupancyRate;
    private String ageRangeText;
    private Double annualFee;
    private String feeRangeText;
    private String phone;
    private String email;
    private String websiteUrl;
    private Double popularityScore;


    private Integer trustScore;
    private Double qualityScore;
    private String trustLevel;
    private Integer foundedYear;
    private Long propertyCount;
    private Integer currentStudentCount;



}
