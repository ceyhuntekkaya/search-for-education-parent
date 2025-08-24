package com.genixo.education.search.dto.content;

import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.dto.user.UserSummaryDto;
import com.genixo.education.search.enumaration.PostStatus;
import com.genixo.education.search.enumaration.PostType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private PostType postType;
    private PostStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;

    // Media content
    private String featuredImageUrl;
    private String videoUrl;
    private String videoThumbnailUrl;
    private Integer videoDurationSeconds;
    private String mediaAttachments; // JSON

    // Settings
    private Boolean allowComments;
    private Boolean allowLikes;
    private Boolean isFeatured;
    private Boolean isPinned;
    private LocalDateTime pinExpiresAt;

    // SEO
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String tags;
    private String hashtags;

    // Engagement metrics
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private Long shareCount;
    private Double engagementScore;

    // Content moderation
    private Boolean isModerated;
    private Boolean isFlagged;
    private Integer flagCount;

    // Analytics
    private Long reachCount;
    private Long impressionCount;
    private Long clickCount;
    private Integer averageReadTimeSeconds;

    // External links
    private String externalUrl;
    private String callToAction;
    private String ctaUrl;

    // Location
    private String locationName;
    private Double latitude;
    private Double longitude;

    // Relationships
    private SchoolSummaryDto school;
    private UserSummaryDto author;
    private Boolean isActive;
    private LocalDateTime createdAt;
}