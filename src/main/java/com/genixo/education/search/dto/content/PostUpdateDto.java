package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {
    private String title;
    private String content;
    private PostStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime expiresAt;

    // Media content
    private String featuredImageUrl;
    private String videoUrl;
    private String videoThumbnailUrl;
    private String mediaAttachments;

    // Settings
    private Boolean allowComments;
    private Boolean allowLikes;
    private Boolean isFeatured;
    private Boolean isPinned;
    private LocalDateTime pinExpiresAt;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String tags;
    private String hashtags;

    // External links
    private String externalUrl;
    private String callToAction;
    private String ctaUrl;
}