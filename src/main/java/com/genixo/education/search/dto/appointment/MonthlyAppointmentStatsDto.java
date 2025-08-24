package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAppointmentStatsDto {
    private Integer year;
    private Integer month;
    private String monthName;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Double completionRate;
    private Integer totalEnrollments;
    private Double enrollmentRate;
}