package com.genixo.education.search.entity.campaign;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.CampaignSchoolStatus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_schools",
        uniqueConstraints = @UniqueConstraint(columnNames = {"campaign_id", "school_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSchool extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedByUser;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignSchoolStatus status = CampaignSchoolStatus.ACTIVE;

    // School-specific customizations
    @Column(name = "custom_discount_amount", precision = 19, scale = 2)
    private BigDecimal customDiscountAmount;

    @Column(name = "custom_discount_percentage")
    private Double customDiscountPercentage;

    @Column(name = "custom_usage_limit")
    private Integer customUsageLimit;

    @Column(name = "custom_start_date")
    private LocalDate customStartDate;

    @Column(name = "custom_end_date")
    private LocalDate customEndDate;

    @Column(name = "custom_terms", columnDefinition = "TEXT")
    private String customTerms;

    // Priority for this school
    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "is_featured_on_school")
    private Boolean isFeaturedOnSchool = false;

    @Column(name = "show_on_homepage")
    private Boolean showOnHomepage = true;

    @Column(name = "show_on_pricing_page")
    private Boolean showOnPricingPage = true;

    // Usage tracking for this school
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "application_count")
    private Integer applicationCount = 0;

    @Column(name = "conversion_count")
    private Integer conversionCount = 0;

    @Column(name = "revenue_generated", precision = 19, scale = 2)
    private BigDecimal revenueGenerated = BigDecimal.ZERO;

    // Performance metrics
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "inquiry_count")
    private Long inquiryCount = 0L;

    @Column(name = "appointment_count")
    private Long appointmentCount = 0L;

    // Approval workflow
    @Column(name = "approved_by_school")
    private Boolean approvedBySchool = true;

    @Column(name = "approved_by_school_user_id")
    private Long approvedBySchoolUserId;

    @Column(name = "approved_by_school_at")
    private LocalDateTime approvedBySchoolAt;

    @Column(name = "school_notes")
    private String schoolNotes;
}