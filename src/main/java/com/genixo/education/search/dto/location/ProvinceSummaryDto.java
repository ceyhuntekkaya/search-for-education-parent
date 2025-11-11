package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceSummaryDto {

    public ProvinceSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Long id;
    private String name;
    private String code;
    private String plateCode;
    private Boolean isMetropolitan;
    private Long schoolCount;
}
