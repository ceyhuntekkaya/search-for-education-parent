package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.entity.pricing.PriceHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTrendsDto {

    private List<PriceHistory> history;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    List<PriceTrendPoint> trendPoints;
}
