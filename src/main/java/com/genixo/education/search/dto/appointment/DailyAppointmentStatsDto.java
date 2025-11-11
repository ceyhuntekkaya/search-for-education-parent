package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAppointmentStatsDto {
    private LocalDate date;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer canceledAppointments;
    private Integer noShowAppointments;
    private Double completionRate;
    private Integer enrollments;
}