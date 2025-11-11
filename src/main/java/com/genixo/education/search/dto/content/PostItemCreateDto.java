package com.genixo.education.search.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.genixo.education.search.enumaration.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostItemCreateDto {
    private Long id;
    private Long postId;
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

    // Image/Video specific
    private Integer width;
    private Integer height;
    private Integer durationSeconds;
    private String videoFormat;

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
}

