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
public class AppointmentSlotBulkCreateDto {
    private Long schoolId;

    /** @deprecated use {@link #staffUserIds} */
    private Long staffUserId;
    private List<Long> staffUserIds;

    /**
     * If {@link #selectedStartTimes} is used, durationMinutes must be provided
     * to compute end-time. If {@link #selectedTimeSlots} is used, duration is
     * derived from each "HH:mm-HH:mm" range (and validated against durationMinutes if provided).
     */
    private Integer durationMinutes;

    /** @deprecated use {@link #appointmentTypes} */
    private AppointmentType appointmentType;
    private List<AppointmentType> appointmentTypes;
    private Boolean onlineMeetingAvailable;

    private List<LocalDate> selectedDates;

    /**
     * Either send "HH:mm" start times (with durationMinutes),
     * or send "HH:mm-HH:mm" ranges via selectedTimeSlots.
     */
    private List<String> selectedStartTimes;
    private List<String> selectedTimeSlots;

    /**
     * If true, overlapping/duplicate slots are skipped; otherwise they produce an error.
     */
    @Builder.Default
    private Boolean skipExisting = true;

    /**
     * If true, any validation/overlap error fails the whole request.
     */
    @Builder.Default
    private Boolean failFast = false;
}

