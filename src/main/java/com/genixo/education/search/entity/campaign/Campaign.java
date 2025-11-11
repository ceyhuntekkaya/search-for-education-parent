package com.genixo.education.search.entity.campaign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.CampaignType;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.TargetAudience;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "campaigns")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Campaign extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    // Discount values
    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount; // Fixed amount discount (1000 TL)

    @Column(name = "discount_percentage")
    private Double discountPercentage; // Percentage discount (10%)

    @Column(name = "max_discount_amount", precision = 19, scale = 2)
    private BigDecimal maxDiscountAmount; // Maximum discount for percentage

    @Column(name = "min_purchase_amount", precision = 19, scale = 2)
    private BigDecimal minPurchaseAmount; // Minimum purchase for discount

    // Campaign period
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "early_bird_end_date")
    private LocalDate earlyBirdEndDate;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    // Enrollment specific dates
    @Column(name = "enrollment_start_date")
    private LocalDate enrollmentStartDate;

    @Column(name = "enrollment_end_date")
    private LocalDate enrollmentEndDate;

    @Column(name = "academic_year")
    private String academicYear; // 2024-2025

    // Campaign status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    // Usage limits
    @Column(name = "usage_limit")
    private Integer usageLimit; // How many times can be used

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "per_user_limit")
    private Integer perUserLimit = 1; // Times per user

    @Column(name = "per_school_limit")
    private Integer perSchoolLimit; // Times per school

    // Target audience
    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience")
    private TargetAudience targetAudience;

    @Column(name = "target_grade_levels")
    private String targetGradeLevels; // JSON array of grade levels

    @Column(name = "target_age_min")
    private Integer targetAgeMin;

    @Column(name = "target_age_max")
    private Integer targetAgeMax;

    @Column(name = "target_new_students_only")
    private Boolean targetNewStudentsOnly = false;

    @Column(name = "target_sibling_discount")
    private Boolean targetSiblingDiscount = false;

    // Promotional content
    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "cta_text")
    private String ctaText; // Call to Action text

    @Column(name = "cta_url")
    private String ctaUrl;

    @Column(name = "badge_text")
    private String badgeText; // "ÖZEL TEKLİF", "SİNİRLİ SÜRE" etc.

    @Column(name = "badge_color")
    private String badgeColor;

    // Terms and conditions
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "fine_print", columnDefinition = "TEXT")
    private String finePrint;

    @Column(name = "exclusions", columnDefinition = "TEXT")
    private String exclusions;

    // SEO
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    // Analytics
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "application_count")
    private Long applicationCount = 0L;

    @Column(name = "conversion_count")
    private Long conversionCount = 0L;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    // Campaign creator
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Additional features
    @Column(name = "free_trial_days")
    private Integer freeTrialDays;

    @Column(name = "installment_options")
    private String installmentOptions; // JSON array

    @Column(name = "payment_deadline_days")
    private Integer paymentDeadlineDays;

    @Column(name = "refund_policy", columnDefinition = "TEXT")
    private String refundPolicy;

    // Special offers
    @Column(name = "free_services")
    private String freeServices; // JSON array of free services

    @Column(name = "bonus_features")
    private String bonusFeatures; // JSON array of bonus features

    @Column(name = "gift_items")
    private String giftItems; // JSON array of gift items

    // Priority and sorting
    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Notification settings
    @Column(name = "send_notifications")
    private Boolean sendNotifications = true;

    @Column(name = "notification_message")
    private String notificationMessage;

    @Column(name = "email_template_id")
    private String emailTemplateId;

    @Column(name = "sms_template_id")
    private String smsTemplateId;

    // Relationships
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<CampaignSchool> campaignSchools = new HashSet<>();

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<CampaignContent> campaignContents = new HashSet<>();

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<CampaignUsage> campaignUsages = new HashSet<>();
}
