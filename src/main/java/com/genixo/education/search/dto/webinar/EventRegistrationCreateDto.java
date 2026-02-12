package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationCreateDto {

    @NotNull(message = "Etkinlik ID zorunludur")
    private Long eventId;

    @NotNull(message = "Öğretmen ID zorunludur")
    private Long teacherId;

    @Size(max = 1000)
    private String registrationNote;
}
