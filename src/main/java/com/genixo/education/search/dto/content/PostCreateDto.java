package com.genixo.education.search.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.genixo.education.search.enumaration.PostStatus;
import com.genixo.education.search.enumaration.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostCreateDto {
    private Long id;
    private Long schoolId;
    private String title;
    private String content;
    private PostType postType;
    private PostStatus status;
    private LocalDateTime scheduledAt;
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
    private String metaTitle;
    private String metaDescription;
    private String tags;
    private String hashtags;

    // External links
    private String externalUrl;
    private String callToAction;
    private String ctaUrl;

    // Location
    private String locationName;
    private Double latitude;
    private Double longitude;

    private List<PostItemCreateDto> items;
}