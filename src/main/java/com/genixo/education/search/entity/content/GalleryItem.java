package com.genixo.education.search.entity.content;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.MediaType;
import com.genixo.education.search.enumaration.ProcessingStatus;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_items")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GalleryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery gallery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedByUser;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "alt_text")
    private String altText;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private MediaType itemType;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type")
    private String mimeType;

    // Image specific
    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "aspect_ratio")
    private String aspectRatio;

    // Video specific
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "video_format")
    private String videoFormat;

    @Column(name = "video_codec")
    private String videoCodec;

    @Column(name = "bitrate")
    private Integer bitrate;

    @Column(name = "frame_rate")
    private Double frameRate;

    // Camera/Device information
    @Column(name = "camera_make")
    private String cameraMake;

    @Column(name = "camera_model")
    private String cameraModel;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Organization
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_cover")
    private Boolean isCover = false;

    @Column(name = "tags")
    private String tags; // Comma separated

    @Column(name = "color_palette", columnDefinition = "JSON")
    private String colorPalette; // Dominant colors

    // Stats
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    // Processing status
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column(name = "processing_error")
    private String processingError;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // Content moderation
    @Column(name = "is_moderated")
    private Boolean isModerated = false;

    @Column(name = "moderation_score")
    private Double moderationScore;

    @Column(name = "moderation_labels", columnDefinition = "JSON")
    private String moderationLabels;

    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "flag_reason")
    private String flagReason;
}
