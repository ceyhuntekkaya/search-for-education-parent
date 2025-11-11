package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffPerformanceDto {
    private Long staffUserId;
    private String staffUserName;
    private String role;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer canceledAppointments;
    private Integer noShowAppointments;
    private Double completionRate;
    private Double cancellationRate;
    private Double noShowRate;
    private Integer enrollmentsGenerated;
    private Double enrollmentConversionRate;
    private Double averageEnrollmentLikelihood;
    private Double averageAppointmentDuration;
    private Double parentSatisfactionRating;
    private String performanceRank;
    private List<String> strengths;
    private List<String> improvementAreas;
}