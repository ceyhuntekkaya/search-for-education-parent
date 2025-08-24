package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentStatisticsDto {
    private Long schoolId;
    private String schoolName;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // Overall metrics
    private Long totalAppointments;
    private Long completedAppointments;
    private Long canceledAppointments;
    private Long noShowAppointments;
    private Long rescheduledAppointments;

    // Conversion metrics
    private Double completionRate;
    private Double cancellationRate;
    private Double noShowRate;
    private Double rescheduleRate;
    private Double enrollmentConversionRate;

    // Outcome metrics
    private Long enrolledCount;
    private Long interestedCount;
    private Long notInterestedCount;
    private Double averageEnrollmentLikelihood;

    // Time metrics
    private Double averageAppointmentDuration;
    private Integer averageRescheduleCount;
    private Double averageResponseTime;

    // Popular choices
    private String mostPopularTimeSlot;
    private String mostPopularDay;
    private AppointmentType mostPopularType;
    private String mostSuccessfulStaff;

    // Trends
    private List<DailyAppointmentStatsDto> dailyStats;
    private List<MonthlyAppointmentStatsDto> monthlyStats;
}