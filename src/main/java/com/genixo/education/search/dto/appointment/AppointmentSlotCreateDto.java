package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlotCreateDto {
    private Long schoolId;
    private Long staffUserId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Integer capacity;
    private AppointmentType appointmentType;
    private String title;
    private String description;
    private String location;
    private Boolean onlineMeetingAvailable;
    private Boolean preparationRequired;
    private String preparationNotes;
    private Boolean isRecurring;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String excludedDates;
    private Integer advanceBookingHours;
    private Integer maxAdvanceBookingDays;
    private Integer cancellationHours;
    private Boolean requiresApproval;
}
