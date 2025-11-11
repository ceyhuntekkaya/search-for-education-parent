package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentExportDto {
    private String exportType; // POSTS, GALLERIES, MESSAGES
    private Long schoolId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format; // JSON, CSV, PDF
    private Boolean includeMedia;
    private Boolean includeComments;
    private Boolean includeAnalytics;
    private String exportUrl;
    private String exportStatus;
    private LocalDateTime exportRequestDate;
    private LocalDateTime exportCompletedDate;
}