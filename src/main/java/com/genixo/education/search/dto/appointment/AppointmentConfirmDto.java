package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentConfirmDto {
    private Long appointmentId;
    private Long confirmedBy;
}
