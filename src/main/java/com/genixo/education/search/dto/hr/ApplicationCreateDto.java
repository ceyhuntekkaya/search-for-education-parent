package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreateDto {

    @NotNull(message = "İlan ID zorunludur")
    private Long jobPostingId;

    @NotNull(message = "Öğretmen profili ID zorunludur")
    private Long teacherProfileId;

    private String coverLetter;

    private List<ApplicationDocumentCreateDto> documents;
}
