package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutcomeAnalysisDto {
    private AppointmentOutcome outcome;
    private String outcomeDisplayName;
    private Integer count;
    private Double percentage;
    private Double averageEnrollmentLikelihood;
    private List<String> commonReasons;
    private List<String> nextStepsRecommendations;
}