package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingSummaryDto {
    private Long id;
    private String positionTitle;
    private String branch;
    private String employmentType;
    private LocalDate applicationDeadline;
    private String status;
    private CampusSummaryDto campus;
}
