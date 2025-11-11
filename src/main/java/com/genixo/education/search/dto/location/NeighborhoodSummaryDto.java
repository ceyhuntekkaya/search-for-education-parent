package com.genixo.education.search.dto.location;

import com.genixo.education.search.enumaration.IncomeLevel;
import com.genixo.education.search.enumaration.NeighborhoodType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborhoodSummaryDto {
    private Long id;
    private String name;
    private String code;
    private NeighborhoodType neighborhoodType;
    private Long schoolCount;
    private IncomeLevel incomeLevel;
    private Integer schoolPreferenceScore;
}