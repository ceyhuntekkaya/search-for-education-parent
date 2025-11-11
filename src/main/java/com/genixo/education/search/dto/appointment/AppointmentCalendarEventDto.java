package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCalendarEventDto {
    private Long appointmentId;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String statusColor;
    private String participantName;
    private String location;
    private Boolean isOnline;
    private Boolean isUrgent;
    private String tooltip;
}
