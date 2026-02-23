package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherExperienceCreateDto {

    @NotBlank(message = "Kurum adı zorunludur")
    @Size(max = 200)
    private String institution;

    @NotBlank(message = "Görev unvanı zorunludur")
    @Size(max = 200)
    private String roleTitle;

    @NotNull(message = "İşe giriş tarihi zorunludur")
    private LocalDate startDate;

    private LocalDate endDate; // null = hâlâ çalışıyor

    private String description; // Görev tanımı / iş açıklaması (metin, opsiyonel)

    private Integer displayOrder;
}
