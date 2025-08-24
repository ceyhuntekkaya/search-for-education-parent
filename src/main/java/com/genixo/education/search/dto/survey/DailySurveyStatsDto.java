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
public class DailySurveyStatsDto {
    private LocalDate date;
    private Long invitationsSent;
    private Long responsesStarted;
    private Long responsesCompleted;
    private Double completionRate;
    private Double averageRating;
    private Integer averageCompletionTime;
}