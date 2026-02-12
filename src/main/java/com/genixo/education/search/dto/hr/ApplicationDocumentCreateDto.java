package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
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
public class ApplicationDocumentCreateDto {

    @NotBlank(message = "Belge adı zorunludur")
    @Size(max = 200)
    private String documentName;

    @NotBlank(message = "Belge URL zorunludur")
    @Size(max = 500)
    private String documentUrl;

    @Size(max = 50)
    private String documentType;

    private Long fileSize;
}
