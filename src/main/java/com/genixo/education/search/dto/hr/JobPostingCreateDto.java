package com.genixo.education.search.dto.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingCreateDto {

    @NotNull(message = "Okul ID zorunludur")
    private Long schoolId;

    @NotBlank(message = "Pozisyon adı zorunludur")
    @Size(max = 200)
    private String positionTitle;

    @NotBlank(message = "Branş zorunludur")
    @Size(max = 100)
    private String branch;

    @Size(max = 50)
    private String employmentType;

    private LocalDate startDate;

    @Size(max = 100)
    private String contractDuration;

    private Integer requiredExperienceYears;

    @Size(max = 50)
    private String requiredEducationLevel;

    private Integer salaryMin;
    private Integer salaryMax;

    @Builder.Default
    private Boolean showSalary = false;

    private String description;

    private LocalDate applicationDeadline;

    @Builder.Default
    private String status = "DRAFT";

    @Builder.Default
    private Boolean isPublic = true;

    private List<Long> provinceIds;
}
