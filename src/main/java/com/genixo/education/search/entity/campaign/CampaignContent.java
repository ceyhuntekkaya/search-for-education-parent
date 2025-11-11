package com.genixo.education.search.entity.campaign;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.CampaignContentType;
import com.genixo.education.search.enumaration.ContentApprovalStatus;
import com.genixo.education.search.enumaration.ContentUsageContext;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_contents")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CampaignContent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private CampaignContentType contentType;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "caption")
    private String caption;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds; // For videos/audio

    @Column(name = "dimensions")
    private String dimensions; // "1920x1080"

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_context")
    private ContentUsageContext usageContext;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "language_code")
    private String languageCode = "tr";

    // Social media specific
    @Column(name = "hashtags")
    private String hashtags;

    @Column(name = "mention_accounts")
    private String mentionAccounts;

    @Column(name = "social_media_platforms")
    private String socialMediaPlatforms; // Which platforms to share

    // A/B testing
    @Column(name = "variant_name")
    private String variantName;

    @Column(name = "is_test_variant")
    private Boolean isTestVariant = false;

    // Performance tracking
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    @Column(name = "share_count")
    private Long shareCount = 0L;

    @Column(name = "engagement_rate")
    private Double engagementRate = 0.0;

    // Content approval
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ContentApprovalStatus approvalStatus = ContentApprovalStatus.PENDING;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Copyright and licensing
    @Column(name = "copyright_owner")
    private String copyrightOwner;

    @Column(name = "license_type")
    private String licenseType;

    @Column(name = "usage_rights", columnDefinition = "TEXT")
    private String usageRights;

    @Column(name = "attribution_required")
    private Boolean attributionRequired = false;

    @Column(name = "attribution_text")
    private String attributionText;
}