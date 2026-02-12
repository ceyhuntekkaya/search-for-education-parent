package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String branch;
    private String profilePhotoUrl;
}
