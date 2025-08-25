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



    public AppointmentStatisticsDto(Long schoolId, String schoolName, LocalDate periodStart, LocalDate periodEnd,
                                    Long totalAppointments, Long completedAppointments, Long canceledAppointments,
                                    Long noShowAppointments, Long rescheduledAppointments, Double completionRate,
                                    Double cancellationRate, Double noShowRate, Double rescheduleRate,
                                    Double enrollmentConversionRate, Long enrolledCount, Long interestedCount,
                                    Long notInterestedCount, Double averageEnrollmentLikelihood,
                                    Double averageAppointmentDuration, Integer averageRescheduleCount,
                                    Double averageResponseTime, String mostPopularTimeSlot, String mostPopularDay,
                                    AppointmentType mostPopularType, String mostSuccessfulStaff) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.canceledAppointments = canceledAppointments;
        this.noShowAppointments = noShowAppointments;
        this.rescheduledAppointments = rescheduledAppointments;
        this.completionRate = completionRate;
        this.cancellationRate = cancellationRate;
        this.noShowRate = noShowRate;
        this.rescheduleRate = rescheduleRate;
        this.enrollmentConversionRate = enrollmentConversionRate;
        this.enrolledCount = enrolledCount;
        this.interestedCount = interestedCount;
        this.notInterestedCount = notInterestedCount;
        this.averageEnrollmentLikelihood = averageEnrollmentLikelihood;
        this.averageAppointmentDuration = averageAppointmentDuration;
        this.averageRescheduleCount = averageRescheduleCount;
        this.averageResponseTime = averageResponseTime;
        this.mostPopularTimeSlot = mostPopularTimeSlot;
        this.mostPopularDay = mostPopularDay;
        this.mostPopularType = mostPopularType;
        this.mostSuccessfulStaff = mostSuccessfulStaff;

        // List'ler null olarak başlatılır, sonra ayrı metodlarla doldurulur
        this.dailyStats = null;
        this.monthlyStats = null;
    }


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