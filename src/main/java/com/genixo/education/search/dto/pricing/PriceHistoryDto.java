package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.PriceChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistoryDto {
    private Long id;
    private Long schoolPricingId;
    private String schoolName;
    private String academicYear;
    private String gradeLevel;
    private String changedByUserName;
    private LocalDateTime changeDate;
    private PriceChangeType changeType;
    private String fieldName;
    private String fieldDisplayName;
    private BigDecimal oldValue;
    private BigDecimal newValue;
    private Double changePercentage;
    private BigDecimal changeAmount;
    private String reason;
    private String changeNotes;
    private LocalDate effectiveDate;
    private String approvedByUserName;
    private LocalDateTime approvedAt;

    // Impact analysis
    private Integer affectedStudentsCount;
    private BigDecimal revenueImpact;
    private String competitiveAnalysis;

    // Communication
    private Boolean parentsNotified;
    private LocalDateTime notificationDate;
    private String notificationMethod;
    private Integer advanceNoticeDays;

    // Rollback information
    private Boolean canRollback;
    private LocalDateTime rollbackDeadline;

    // Formatted values
    private String formattedOldValue;
    private String formattedNewValue;
    private String formattedChangeAmount;
    private String changeDirection; // INCREASE, DECREASE, NO_CHANGE
}