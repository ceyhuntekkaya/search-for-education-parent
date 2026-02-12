package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationNoteCreateDto {

    @NotNull(message = "Başvuru ID zorunludur")
    private Long applicationId;

    @NotBlank(message = "Not metni zorunludur")
    private String noteText;
}
