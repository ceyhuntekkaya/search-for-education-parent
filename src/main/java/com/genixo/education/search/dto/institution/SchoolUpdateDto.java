package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolUpdateDto {
    private String name;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
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

    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String linkedinUrl;
    private String youtubeUrl;
}