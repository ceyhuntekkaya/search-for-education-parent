package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSummaryDto {
    private Long id;
    private String appointmentNumber;
    private String schoolName;
    private String parentName;
    private String studentName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private AppointmentType appointmentType;
    private String location;
    private Boolean isOnline;
    private String staffUserName;
    private AppointmentOutcome outcome;
    private Boolean followUpRequired;
    private String statusDisplayName;
    private String formattedDateTime;
    private String timeUntilAppointment;
}
