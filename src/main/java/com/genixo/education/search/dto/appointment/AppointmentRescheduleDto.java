package com.genixo.education.search.dto.appointment;

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
public class AppointmentRescheduleDto {
    private Long appointmentId;
    private Long newAppointmentSlotId;
    private String rescheduleReason;
}
