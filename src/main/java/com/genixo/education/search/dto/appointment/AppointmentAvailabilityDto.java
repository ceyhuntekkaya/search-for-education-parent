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
public class AppointmentAvailabilityDto {
    private Long schoolId;
    private String schoolName;
    private LocalDate date;
    private List<AvailableSlotDto> availableSlots;
    private Integer totalSlots;
    private Integer bookedSlots;
    private Integer availableCount;
    private String availability; // FULLY_BOOKED, LIMITED, AVAILABLE, ABUNDANT
}