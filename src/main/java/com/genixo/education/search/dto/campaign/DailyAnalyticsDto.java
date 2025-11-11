package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAnalyticsDto {
    private LocalDate date;
    private Long views;
    private Long clicks;
    private Long applications;
    private Long conversions;
    private Double conversionRate;
    private BigDecimal revenueGenerated;
}
