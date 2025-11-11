package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlotCreateDto {
    private Long schoolId;
    private Long staffUserId;
    private Integer durationMinutes;
    private AppointmentType appointmentType;
    private Boolean onlineMeetingAvailable;
    private LocalDateTime slotDate;
}
