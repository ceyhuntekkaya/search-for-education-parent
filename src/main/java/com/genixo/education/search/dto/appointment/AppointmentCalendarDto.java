package com.genixo.education.search.dto.appointment;

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
public class AppointmentCalendarDto {
    private LocalDate date;
    private List<AppointmentCalendarEventDto> events;
    private Integer totalAppointments;
    private Integer availableSlots;
    private Boolean hasConflicts;
}