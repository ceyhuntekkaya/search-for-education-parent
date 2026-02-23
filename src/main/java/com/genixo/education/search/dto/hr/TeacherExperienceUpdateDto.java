package com.genixo.education.search.dto.hr;

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
public class TeacherExperienceUpdateDto {

    @Size(max = 200)
    private String institution;

    @Size(max = 200)
    private String roleTitle;

    private LocalDate startDate;
    private LocalDate endDate;
    private String description; // Görev tanımı / iş açıklaması
    private Integer displayOrder; // Sıralama (küçük önce)
}
