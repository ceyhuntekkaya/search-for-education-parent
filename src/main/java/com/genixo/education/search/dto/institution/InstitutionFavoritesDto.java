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
public class InstitutionFavoritesDto {
    private Long userId;
    private List<SchoolSummaryDto> favoriteSchools;
    private List<CampusSummaryDto> favoriteCampuses;
    private List<BrandSummaryDto> favoriteBrands;
    private Integer totalFavorites;
}