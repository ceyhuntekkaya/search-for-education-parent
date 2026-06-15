package com.genixo.education.search.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentAppointmentNoteCreateDto {

    @NotBlank(message = "Note content is required")
    private String note;
}
