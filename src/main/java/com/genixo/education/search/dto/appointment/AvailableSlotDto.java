package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableSlotDto {
    private Long slotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private AppointmentType appointmentType;
    private String location;
    private Boolean isOnline;
    private String staffUserName;
    private Integer availableCapacity;
    private Boolean requiresApproval;
    private String timeRange;
    private Boolean isRecommended;
}