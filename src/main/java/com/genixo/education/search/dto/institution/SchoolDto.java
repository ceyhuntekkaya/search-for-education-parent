package com.genixo.education.search.dto.institution;

import java.time.LocalDateTime;
import java.util.List;

import com.genixo.education.search.dto.pricing.PricingSummaryDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String coverImageUrl;

    // Contact Information
    private String email;
    private String phone;
    private String extension;

    // Education Information
    private Integer minAge;
    private Integer maxAge;
    private Integer capacity;
    private Integer currentStudentCount;
    private Integer classSizeAverage;

    // Academic Information
    private String curriculumType;
    private String languageOfInstruction;
    private String foreignLanguages;

    // Fees
    private Double registrationFee;
    private Double monthlyFee;
    private Double annualFee;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Statistics
    private Long viewCount;
    private Double ratingAverage;
    private Long ratingCount;
    private Long likeCount;
    private Long postCount;

    // Relationships
    private CampusSummaryDto campus;
    private InstitutionTypeDto institutionType;
    private List<InstitutionPropertyValueDto> propertyValues;
    private List<PricingSummaryDto> pricings;
    private List<CampaignSummaryDto> activeCampaigns;
    private Boolean isActive;
    private LocalDateTime createdAt;
}