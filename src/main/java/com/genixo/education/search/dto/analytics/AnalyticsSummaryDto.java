package com.genixo.education.search.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryDto {
    private LocalDate date;
    private Long pageViews;
    private Long uniqueVisitors;
    private Long appointmentRequests;
    private Long messageInquiries;
    private Double conversionRate;
    private Double averageRating;
    private String institutionName;
    private String institutionType;
}