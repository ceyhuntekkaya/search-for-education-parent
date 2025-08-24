package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.ParticipantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentParticipantCreateDto {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private ParticipantType participantType;
    private String notes;
}