package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotAnalysisDto {
    private String timeSlot;
    private DayOfWeek dayOfWeek;
    private String dayOfWeekName;
    private Integer totalSlots;
    private Integer bookedSlots;
    private Integer completedAppointments;
    private Double utilizationRate;
    private Double completionRate;
    private Double enrollmentRate;
    private Boolean isPopular;
    private Boolean isRecommended;
}