package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherEducationCreateDto {

    @NotBlank(message = "Eğitim seviyesi zorunludur")
    @Size(max = 50)
    private String educationLevel; // Ön Lisans, Lisans, Yüksek Lisans, Doktora

    @NotBlank(message = "Kurum adı zorunludur")
    @Size(max = 200)
    private String institution;

    @Size(max = 200)
    private String department;

    private Integer startYear;
    private Integer endYear;
    private Integer displayOrder;
}
