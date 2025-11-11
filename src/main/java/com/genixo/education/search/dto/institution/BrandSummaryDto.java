package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandSummaryDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private Double ratingAverage;
    private Integer campusCount;
    private Integer schoolCount;
}