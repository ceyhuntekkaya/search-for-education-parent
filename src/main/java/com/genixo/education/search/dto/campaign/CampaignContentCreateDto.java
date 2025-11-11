package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignContentType;
import com.genixo.education.search.enumaration.ContentUsageContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignContentCreateDto {
    private Long campaignId;
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

    // Copyright and licensing
    private String copyrightOwner;
    private String licenseType;
    private String usageRights;
    private Boolean attributionRequired;
    private String attributionText;
}
