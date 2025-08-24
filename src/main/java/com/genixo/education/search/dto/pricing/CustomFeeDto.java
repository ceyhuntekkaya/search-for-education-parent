package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.CustomFeeStatus;
import com.genixo.education.search.enumaration.CustomFeeType;
import com.genixo.education.search.enumaration.PaymentFrequency;
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
public class CustomFeeDto {
    private Long id;
    private Long schoolPricingId;
    private String schoolName;
    private String academicYear;
    private String feeName;
    private String feeDescription;
    private BigDecimal feeAmount;
    private CustomFeeType feeType;
    private PaymentFrequency feeFrequency;
    private Boolean isMandatory;
    private Boolean isRefundable;
    private Boolean appliesToNewStudents;
    private Boolean appliesToExistingStudents;
    private String appliesToGrades;
    private Integer minimumAge;
    private Integer maximumAge;

    // Validity
    private LocalDate validFrom;
    private LocalDate validUntil;
    private CustomFeeStatus status;

    // Payment terms
    private Integer dueDateOffsetDays;
    private Double lateFeePercentage;
    private Boolean installmentAllowed;
    private Integer maxInstallments;

    // Conditions
    private Boolean discountEligible;
    private Boolean scholarshipApplicable;

    // Documentation
    private Boolean documentationRequired;
    private String requiredDocuments;
    private String feePolicy;

    // Display
    private Boolean displayOnInvoice;
    private Integer displayOrder;
    private Boolean parentNotificationRequired;
    private Integer advanceNoticeDays;

    // Analytics
    private Double collectionRate;
    private BigDecimal totalCollected;
    private Integer studentsCharged;
    private Integer studentsPaid;
    private Double averagePaymentDelayDays;

    // Metadata
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Formatted values
    private String formattedFeeAmount;
    private String frequencyDisplayName;
    private String applicabilityDescription;
}