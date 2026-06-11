package com.genixo.education.search.dto.institution;


import com.genixo.education.search.dto.location.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String coverImageUrl;

    // Contact Information
    private String email;
    private String phone;
    private String fax;
    private String websiteUrl;

    // Address Information
    private String addressLine1;
    private String addressLine2;
    private DistrictSummaryDto district;
    private ProvinceSummaryDto province;
    private NeighborhoodSummaryDto neighborhood;
    private String postalCode;
    private CountrySummaryDto country;
    private Double latitude;
    private Double longitude;

    // Location details
    private LocationHierarchyDto location;

    // Social Media
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String linkedinUrl;
    private String youtubeUrl;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Statistics
    private Long viewCount;
    private Double ratingAverage;
    private Long ratingCount;
//
    /// / Relationships
    private List<CampusSummaryDto> campuses;
    private Boolean isActive;
    private LocalDateTime createdAt;


    private Integer establishedYear;
    private Boolean isSubscribed;

    private BrandSummaryDto brand;
}