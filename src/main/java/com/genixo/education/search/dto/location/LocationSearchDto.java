package com.genixo.education.search.dto.location;

import com.genixo.education.search.enumaration.IncomeLevel;
import com.genixo.education.search.enumaration.SocioeconomicLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSearchDto {
    private String searchTerm;
    private String locationType; // COUNTRY, PROVINCE, DISTRICT, NEIGHBORHOOD
    private Long countryId;
    private Long provinceId;
    private Long districtId;
    private Boolean isActive;

    // Filters
    private Boolean hasSchools;
    private Integer minSchoolCount;
    private SocioeconomicLevel minSocioeconomicLevel;
    private IncomeLevel minIncomeLevel;
    private Boolean hasMetroStation;
    private Boolean hasUniversity;
    private Boolean isMetropolitan;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
