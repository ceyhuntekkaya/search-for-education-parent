package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyAssignmentDto {
    private Long surveyId;
    private Long attendId;
    private Long schoolId;
    private Long appointmentId;
}
