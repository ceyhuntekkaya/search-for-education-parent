package com.genixo.education.search.dto.institution;

import com.genixo.education.search.dto.location.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusCreateDto {
    private Long brandId;
    private String name;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String email;
    private String phone;
    private String fax;
    private String websiteUrl;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    // Location IDs
    private CountrySummaryDto country;
    private ProvinceSummaryDto province;
    private DistrictSummaryDto district;
    private NeighborhoodSummaryDto neighborhood;

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

    private Integer establishedYear;
}