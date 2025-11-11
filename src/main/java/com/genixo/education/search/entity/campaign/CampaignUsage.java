package com.genixo.education.search.entity.campaign;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.CampaignUsageStatus;
import com.genixo.education.search.enumaration.CampaignUsageType;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_usages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Parent who used the campaign
    private User user;

    @Column(name = "usage_date", nullable = false)
    private LocalDateTime usageDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false)
    private CampaignUsageType usageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignUsageStatus status = CampaignUsageStatus.PENDING;

    @Column(name = "original_amount", precision = 19, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", precision = 19, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "promo_code_used")
    private String promoCodeUsed;

    // Student information
    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_age")
    private Integer studentAge;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "enrollment_year")
    private String enrollmentYear;

    // Contact information
    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "parent_phone")
    private String parentPhone;

    // Tracking information
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "referrer_url")
    private String referrerUrl;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    // Approval and processing
    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    // Follow-up
    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "follow_up_completed")
    private Boolean followUpCompleted = false;

    // Related entities
    @Column(name = "appointment_id")
    private Long appointmentId; // If campaign usage led to appointment

    @Column(name = "enrollment_id")
    private Long enrollmentId; // If campaign usage led to enrollment

    @Column(name = "invoice_id")
    private Long invoiceId; // If campaign usage generated invoice

    // Validation
    @Column(name = "validation_code")
    private String validationCode;

    @Column(name = "validation_expires_at")
    private LocalDateTime validationExpiresAt;

    @Column(name = "is_validated")
    private Boolean isValidated = false;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;
}
