package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAppointmentOperationDto {
    private String operation; // CONFIRM, CANCEL, RESCHEDULE, UPDATE_STATUS, SEND_REMINDERS
    private List<Long> appointmentIds;
    private AppointmentStatus newStatus;
    private String reason;
    private Boolean notifyParticipants;
    private Long newAppointmentSlotId;
    private LocalDate newAppointmentDate;
    private LocalTime newStartTime;
    private LocalTime newEndTime;
    private String customMessage;
}
