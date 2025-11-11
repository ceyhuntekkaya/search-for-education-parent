package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolStatusResponse {
    private Long schoolId;
    private Boolean isInAnyList;
    private List<ParentSchoolListSummaryResponse> lists;
}