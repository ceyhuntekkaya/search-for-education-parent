package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusUpdateDto {

    @NotBlank(message = "Durum zorunludur")
    private String status;
}
