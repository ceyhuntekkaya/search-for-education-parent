package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignUsageStatus;
import com.genixo.education.search.enumaration.CampaignUsageType;
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
public class CampaignUsageDto {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private Long schoolId;
    private String schoolName;
    private Long userId;
    private String userFullName;
    private LocalDateTime usageDate;
    private CampaignUsageType usageType;
    private CampaignUsageStatus status;

    // Financial info
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String promoCodeUsed;

    // Student information
    private String studentName;
    private Integer studentAge;
    private String gradeLevel;
    private String enrollmentYear;

    // Contact information
    private String parentName;
    private String parentEmail;
    private String parentPhone;

    // Tracking information
    private String ipAddress;
    private String userAgent;
    private String referrerUrl;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;

    // Processing
    private String approvedByUserName;
    private LocalDateTime approvedAt;
    private LocalDateTime processedAt;
    private String notes;

    // Follow-up
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private Boolean followUpCompleted;

    // Related entities
    private Long appointmentId;
    private Long enrollmentId;
    private Long invoiceId;

    // Validation
    private String validationCode;
    private LocalDateTime validationExpiresAt;
    private Boolean isValidated;
    private LocalDateTime validatedAt;

    // Formatted values
    private String formattedOriginalAmount;
    private String formattedDiscountAmount;
    private String formattedFinalAmount;
    private String savingsAmount;
    private String savingsPercentage;
}