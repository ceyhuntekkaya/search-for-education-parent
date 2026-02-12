package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long id;
    private Long jobPostingId;
    private JobPostingSummaryDto jobPosting;
    private Long teacherId;
    private TeacherProfileSummaryDto teacher;
    private String coverLetter;
    private String status;
    private Boolean isWithdrawn;
    private List<ApplicationNoteDto> notes;
    private List<ApplicationDocumentDto> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
