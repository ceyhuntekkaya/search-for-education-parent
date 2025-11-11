package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandCreateDto {
    private String name;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String websiteUrl;
    private String email;
    private String phone;
    private Integer foundedYear;

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
}