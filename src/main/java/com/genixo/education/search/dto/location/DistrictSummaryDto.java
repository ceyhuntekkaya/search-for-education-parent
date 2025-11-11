package com.genixo.education.search.dto.location;

import com.genixo.education.search.enumaration.DistrictType;
import com.genixo.education.search.enumaration.SocioeconomicLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictSummaryDto {

    public DistrictSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Long id;
    private String name;
    private String code;
    private DistrictType districtType;
    private Boolean isCentral;
    private Long schoolCount;
    private SocioeconomicLevel socioeconomicLevel;
}