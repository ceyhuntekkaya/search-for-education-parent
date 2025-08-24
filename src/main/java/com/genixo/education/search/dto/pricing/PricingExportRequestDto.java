package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingExportRequestDto {
    private String format;
    private List<Long> schoolIds;
    private Boolean includeInactive;
}
