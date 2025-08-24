package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatisfactionTrendDto {
    private LocalDate date;
    private Double overallSatisfaction;
    private Double cleanlinessRating;
    private Double staffRating;
    private Double facilitiesRating;
    private Double communicationRating;
    private Long responseCount;
    private String trendDirection; // UP, DOWN, STABLE
}