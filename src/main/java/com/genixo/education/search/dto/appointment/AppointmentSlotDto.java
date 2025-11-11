package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlotDto {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long staffUserId;
    private String staffUserName;
    // private DayOfWeek dayOfWeek;
    // private LocalTime startTime;
    // private LocalTime endTime;
    private Integer durationMinutes;
    // private Integer capacity;
    private AppointmentType appointmentType;
    // private String title;
    // private String description;
    // private String location;
    private Boolean onlineMeetingAvailable;
    // private Boolean preparationRequired;
    // private String preparationNotes;

    // Slot availability
    // private Boolean isRecurring;
    // private LocalDate validFrom;
    // private LocalDate validUntil;
    // private String excludedDates;

    // Booking rules
    private Integer advanceBookingHours;
    private Integer maxAdvanceBookingDays;
    private Integer cancellationHours;
    private Boolean requiresApproval;

    // Calculated fields
    // private String timeRange;
    private String dayOfWeekName;
    private Boolean isAvailable;
    // private Integer availableCapacity;
    // private Integer bookedCount;
    // private List<LocalDate> nextAvailableDates;

    // Metadata
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime slotDate;
    private AppointmentDto appointment;
}
