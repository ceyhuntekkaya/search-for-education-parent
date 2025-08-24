package com.genixo.education.search.dto.institution;

import com.genixo.education.search.dto.location.DistrictSummaryDto;
import com.genixo.education.search.dto.location.ProvinceSummaryDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusSummaryDto {



    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private ProvinceSummaryDto province;
    private DistrictSummaryDto district;
    private Double ratingAverage;
    private Long schoolCount;
    private Boolean isSubscribed;
}