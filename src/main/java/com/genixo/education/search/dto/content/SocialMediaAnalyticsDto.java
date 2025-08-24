package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialMediaAnalyticsDto {
    private Long schoolId;
    private String schoolName;
    private LocalDate analyticsDate;

    // Post metrics
    private Long totalPosts;
    private Long publishedPosts;
    private Long scheduledPosts;
    private Long draftPosts;

    // Engagement metrics
    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private Long totalViews;
    private Double engagementRate;

    // Growth metrics
    private Long newFollowers;
    private Long newLikes;
    private Long newComments;

    // Content performance
    private PostSummaryDto topPost;
    private List<PostSummaryDto> trendingPosts;

    // Gallery metrics
    private Long totalGalleries;
    private Long totalGalleryItems;
    private Long galleryViews;
    private Long galleryDownloads;
}