package com.genixo.education.search.entity.content;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.PostStatus;
import com.genixo.education.search.enumaration.PostType;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// ========================= POST =========================
@Entity
@Table(name = "posts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User authorUser;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType = PostType.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Media content
    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_thumbnail_url")
    private String videoThumbnailUrl;

    @Column(name = "video_duration_seconds")
    private Integer videoDurationSeconds;

    // Multiple media attachments (JSON array)
    @Column(name = "media_attachments", columnDefinition = "JSON")
    private String mediaAttachments;

    // Post settings
    @Column(name = "allow_comments")
    private Boolean allowComments = true;

    @Column(name = "allow_likes")
    private Boolean allowLikes = true;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "pin_expires_at")
    private LocalDateTime pinExpiresAt;

    // SEO and social media
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "tags")
    private String tags; // Comma separated

    @Column(name = "hashtags")
    private String hashtags; // Comma separated

    // Engagement metrics
    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "comment_count")
    private Long commentCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "share_count")
    private Long shareCount = 0L;

    @Column(name = "engagement_score")
    private Double engagementScore = 0.0;

    // Content moderation
    @Column(name = "is_moderated")
    private Boolean isModerated = false;

    @Column(name = "moderated_by")
    private Long moderatedBy;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Column(name = "moderation_notes")
    private String moderationNotes;

    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "flag_count")
    private Integer flagCount = 0;

    // Analytics
    @Column(name = "reach_count")
    private Long reachCount = 0L;

    @Column(name = "impression_count")
    private Long impressionCount = 0L;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "average_read_time_seconds")
    private Integer averageReadTimeSeconds;

    // External links
    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "call_to_action")
    private String callToAction;

    @Column(name = "cta_url")
    private String ctaUrl;

    // Location
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Relationships
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostComment> comments = new HashSet<>();
}
