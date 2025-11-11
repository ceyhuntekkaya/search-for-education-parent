package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.CancelledByType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCancelDto {
    private Long appointmentId;
    private String cancellationReason;
    private CancelledByType canceledByType;
}