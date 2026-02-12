package com.genixo.education.search.dto.hr;

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
public class JobPostingUpdateDto {

    private String positionTitle;

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
    private Boolean showSalary;
    private String description;
    private LocalDate applicationDeadline;
    private String status;
    private Boolean isPublic;
    private List<Long> provinceIds;
}
