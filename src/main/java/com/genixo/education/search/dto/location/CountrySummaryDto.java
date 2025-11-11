package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountrySummaryDto {
    private Long id;
    private String name;
    private String isoCode2;
    private String flagEmoji;
    private String phoneCode;
    private Boolean isSupported;
}