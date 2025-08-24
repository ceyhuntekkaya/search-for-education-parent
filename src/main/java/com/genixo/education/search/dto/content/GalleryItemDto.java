package com.genixo.education.search.dto.content;

import com.genixo.education.search.dto.user.UserSummaryDto;
import com.genixo.education.search.enumaration.MediaType;
import com.genixo.education.search.enumaration.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItemDto {
    private Long id;
    private Long galleryId;
    private String title;
    private String description;
    private String altText;
    private MediaType itemType;
    private String fileUrl;
    private String thumbnailUrl;
    private String fileName;
    private String originalFileName;
    private Long fileSizeBytes;
    private String mimeType;

    // Image specific
    private Integer width;
    private Integer height;
    private String aspectRatio;

    // Video specific
    private Integer durationSeconds;
    private String videoFormat;
    private String videoCodec;
    private Integer bitrate;
    private Double frameRate;

    // Camera/Device information
    private String cameraMake;
    private String cameraModel;
    private LocalDateTime takenAt;

    // Location
    private String locationName;
    private Double latitude;
    private Double longitude;

    // Organization
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isCover;
    private String tags;
    private String colorPalette; // JSON

    // Stats
    private Long viewCount;
    private Long downloadCount;
    private Long likeCount;

    // Processing
    private ProcessingStatus processingStatus;
    private String processingError;
    private LocalDateTime processedAt;

    // Moderation
    private Boolean isModerated;
    private Double moderationScore;
    private String moderationLabels; // JSON
    private Boolean isFlagged;
    private String flagReason;

    // Relationships
    private UserSummaryDto uploadedByUser;
    private Boolean isActive;
    private LocalDateTime createdAt;
}