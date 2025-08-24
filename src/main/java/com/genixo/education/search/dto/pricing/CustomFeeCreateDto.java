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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFeeCreateDto {
    private Long schoolPricingId;
    private Long createdByUserId;
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
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Integer dueDateOffsetDays;
    private Double lateFeePercentage;
    private Boolean installmentAllowed;
    private Integer maxInstallments;
    private Boolean discountEligible;
    private Boolean scholarshipApplicable;
    private Boolean documentationRequired;
    private String requiredDocuments;
    private String feePolicy;
    private Boolean displayOnInvoice;
    private Integer displayOrder;
    private Boolean parentNotificationRequired;
    private Integer advanceNoticeDays;

    private Boolean requiresApproval;
    private CustomFeeStatus status;
}