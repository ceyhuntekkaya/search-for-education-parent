package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingDto {
    private Long id;
    private Long schoolId;
    private CampusSummaryDto campus;
    private String positionTitle;
    private String branch;
    private String employmentType;
    private LocalDate startDate;
    private String contractDuration;
    private Integer requiredExperienceYears;
    private String requiredEducationLevel;
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean showSalary;
    private String description;
    private LocalDate applicationDeadline;
    private String status;
    private Boolean isPublic;
    private List<ProvinceSummaryDto> provinces;
    private Integer applicationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
