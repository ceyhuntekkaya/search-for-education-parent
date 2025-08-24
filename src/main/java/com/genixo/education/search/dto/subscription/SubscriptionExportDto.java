package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionExportDto {
    private String exportType; // SUBSCRIPTIONS, PAYMENTS, INVOICES, ANALYTICS
    private String format; // CSV, EXCEL, PDF, JSON
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> includedFields;
    private Map<String, Object> filters;

    // Export options
    private Boolean includeInactiveSubscriptions;
    private Boolean includePaymentDetails;
    private Boolean includeUsageMetrics;
    private Boolean includeBillingHistory;

    // Status
    private String exportStatus; // REQUESTED, PROCESSING, COMPLETED, FAILED
    private String downloadUrl;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private Long fileSizeBytes;

    // Security
    private Boolean passwordProtected;
    private LocalDateTime expiresAt;
}