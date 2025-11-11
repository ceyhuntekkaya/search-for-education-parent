package com.genixo.education.search.dto.campaign;

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
public class CampaignUsageCreateDto {
    private Long campaignId;
    private Long schoolId;
    private Long userId;
    private CampaignUsageType usageType;

    // Financial info
    private BigDecimal originalAmount;
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

    // Additional info
    private String notes;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
}
