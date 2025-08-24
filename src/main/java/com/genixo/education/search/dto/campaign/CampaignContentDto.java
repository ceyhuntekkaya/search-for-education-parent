package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignContentType;
import com.genixo.education.search.enumaration.ContentApprovalStatus;
import com.genixo.education.search.enumaration.ContentUsageContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignContentDto {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private CampaignContentType contentType;
    private String title;
    private String content;
    private String mediaUrl;
    private String thumbnailUrl;
    private String altText;
    private String caption;
    private Long fileSizeBytes;
    private String mimeType;
    private Integer durationSeconds;
    private String dimensions;
    private ContentUsageContext usageContext;
    private Integer sortOrder;
    private Boolean isPrimary;
    private String languageCode;

    // Social media specific
    private String hashtags;
    private String mentionAccounts;
    private String socialMediaPlatforms;

    // A/B testing
    private String variantName;
    private Boolean isTestVariant;

    // Performance tracking
    private Long viewCount;
    private Long clickCount;
    private Long downloadCount;
    private Long shareCount;
    private Double engagementRate;

    // Content approval
    private ContentApprovalStatus approvalStatus;
    private String approvedByUserName;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    // Copyright and licensing
    private String copyrightOwner;
    private String licenseType;
    private String usageRights;
    private Boolean attributionRequired;
    private String attributionText;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}