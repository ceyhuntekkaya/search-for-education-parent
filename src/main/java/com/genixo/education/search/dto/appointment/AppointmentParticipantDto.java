package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AttendanceStatus;
import com.genixo.education.search.enumaration.ParticipantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentParticipantDto {
    private Long id;
    private Long appointmentId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private ParticipantType participantType;
    private AttendanceStatus attendanceStatus;
    private String notes;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    // Calculated fields
    private String participantTypeDisplayName;
    private String attendanceStatusDisplayName;
    private Integer attendanceDurationMinutes;
}
