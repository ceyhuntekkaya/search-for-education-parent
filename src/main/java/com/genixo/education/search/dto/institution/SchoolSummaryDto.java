package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSummaryDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String institutionTypeName;
    private Integer minAge;
    private Integer maxAge;
    private Double monthlyFee;
    private Double ratingAverage;
    private Long ratingCount;
    private Boolean hasActiveCampaigns;
}