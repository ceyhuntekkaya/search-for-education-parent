package com.genixo.education.search.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsExportDto {
    private String exportId;
    private String exportName;
    private String exportType; // ANALYTICS, VISITORS, SEARCHES, PERFORMANCE
    private String format; // CSV, EXCEL, JSON, PDF
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> includedMetrics;
    private Map<String, Object> filters;

    // Export status
    private String status; // REQUESTED, PROCESSING, COMPLETED, FAILED
    private Integer progress; // 0-100
    private String downloadUrl;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private Long fileSizeBytes;

    // Requester info
    private String requestedBy;
    private String requestedByEmail;

    // Sharing
    private Boolean isPublic;
    private String shareUrl;
    private LocalDateTime expiresAt;
}