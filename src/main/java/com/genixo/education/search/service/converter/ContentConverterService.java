package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.content.*;
import com.genixo.education.search.entity.content.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.util.ConversionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentConverterService {

    private final UserConverterService userConverterService;
    private final InstitutionConverterService institutionConverterService;
    private final ObjectMapper objectMapper;


    public PostDto mapPostToDto(Post entity) {
        if (entity == null) {
            return null;
        }

        return PostDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .postType(entity.getPostType())
                .status(entity.getStatus())
                .scheduledAt(entity.getScheduledAt())
                .publishedAt(entity.getPublishedAt())
                .expiresAt(entity.getExpiresAt())

                // Media content
                .featuredImageUrl(entity.getFeaturedImageUrl())
                .videoUrl(entity.getVideoUrl())
                .videoThumbnailUrl(entity.getVideoThumbnailUrl())
                .videoDurationSeconds(entity.getVideoDurationSeconds())
                .mediaAttachments(entity.getMediaAttachments())

                // Settings
                .allowComments(ConversionUtils.defaultIfNull(entity.getAllowComments(), true))
                .allowLikes(ConversionUtils.defaultIfNull(entity.getAllowLikes(), true))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .isPinned(ConversionUtils.defaultIfNull(entity.getIsPinned(), false))
                .pinExpiresAt(entity.getPinExpiresAt())

                // SEO
                .slug(entity.getSlug())
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .tags(entity.getTags())
                .hashtags(entity.getHashtags())

                // Engagement metrics
                .likeCount(ConversionUtils.defaultIfNull(entity.getLikeCount(), 0L))
                .commentCount(ConversionUtils.defaultIfNull(entity.getCommentCount(), 0L))
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .shareCount(ConversionUtils.defaultIfNull(entity.getShareCount(), 0L))
                .engagementScore(ConversionUtils.defaultIfNull(entity.getEngagementScore(), 0.0))

                // Content moderation
                .isModerated(ConversionUtils.defaultIfNull(entity.getIsModerated(), false))
                .isFlagged(ConversionUtils.defaultIfNull(entity.getIsFlagged(), false))
                .flagCount(ConversionUtils.defaultIfNull(entity.getFlagCount(), 0))

                // Analytics
                .reachCount(ConversionUtils.defaultIfNull(entity.getReachCount(), 0L))
                .impressionCount(ConversionUtils.defaultIfNull(entity.getImpressionCount(), 0L))
                .clickCount(ConversionUtils.defaultIfNull(entity.getClickCount(), 0L))
                .averageReadTimeSeconds(entity.getAverageReadTimeSeconds())

                // External links
                .externalUrl(entity.getExternalUrl())
                .callToAction(entity.getCallToAction())
                .ctaUrl(entity.getCtaUrl())

                // Location
                .locationName(entity.getLocationName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())

                // Relationships
                .school(institutionConverterService.mapSchoolToSummaryDto(entity.getSchool()))
                .author(userConverterService.mapUserToSummaryDto(entity.getAuthorUser()))

                // Base entity fields
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                // Items
                .items(mapPostItemsToDto(entity.getItems().stream().toList()))
                .build();
    }

    public PostSummaryDto mapPostToSummaryDto(Post entity) {
        if (entity == null) {
            return null;
        }

        return PostSummaryDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .postType(entity.getPostType())
                .status(entity.getStatus())
                .featuredImageUrl(entity.getFeaturedImageUrl())
                .publishedAt(entity.getPublishedAt())
                .likeCount(ConversionUtils.defaultIfNull(entity.getLikeCount(), 0L))
                .commentCount(ConversionUtils.defaultIfNull(entity.getCommentCount(), 0L))
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .isPinned(ConversionUtils.defaultIfNull(entity.getIsPinned(), false))
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .authorName(entity.getAuthorUser() != null ?
                        entity.getAuthorUser().getFirstName() + " " + entity.getAuthorUser().getLastName() : null)
                .build();
    }

    public Post mapPostCreateDtoToEntity(PostCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Post entity = new Post();
        updatePostEntityFromCreateDto(dto, entity);
        return entity;
    }

    public void updatePostEntityFromCreateDto(PostCreateDto dto, Post entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPostType(ConversionUtils.defaultIfNull(dto.getPostType(), PostType.TEXT));
        entity.setStatus(ConversionUtils.defaultIfNull(dto.getStatus(), PostStatus.DRAFT));
        entity.setScheduledAt(dto.getScheduledAt());
        entity.setExpiresAt(dto.getExpiresAt());

        // Media content
        entity.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setVideoThumbnailUrl(dto.getVideoThumbnailUrl());
        entity.setVideoDurationSeconds(dto.getVideoDurationSeconds());
        entity.setMediaAttachments(dto.getMediaAttachments());

        // Settings
        entity.setAllowComments(ConversionUtils.defaultIfNull(dto.getAllowComments(), true));
        entity.setAllowLikes(ConversionUtils.defaultIfNull(dto.getAllowLikes(), true));
        entity.setIsFeatured(ConversionUtils.defaultIfNull(dto.getIsFeatured(), false));
        entity.setIsPinned(ConversionUtils.defaultIfNull(dto.getIsPinned(), false));
        entity.setPinExpiresAt(dto.getPinExpiresAt());

        // SEO - Generate slug if not provided
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        }
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setTags(dto.getTags());
        entity.setHashtags(dto.getHashtags());

        // External links
        entity.setExternalUrl(dto.getExternalUrl());
        entity.setCallToAction(dto.getCallToAction());
        entity.setCtaUrl(dto.getCtaUrl());

        // Location
        entity.setLocationName(dto.getLocationName());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }

    public void updatePostEntityFromUpdateDto(PostUpdateDto dto, Post entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
            entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        }

        if (StringUtils.hasText(dto.getContent())) {
            entity.setContent(dto.getContent());
        }

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        entity.setScheduledAt(dto.getScheduledAt());
        entity.setExpiresAt(dto.getExpiresAt());

        // Media content
        entity.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setVideoThumbnailUrl(dto.getVideoThumbnailUrl());
        entity.setMediaAttachments(dto.getMediaAttachments());

        // Settings
        if (dto.getAllowComments() != null) {
            entity.setAllowComments(dto.getAllowComments());
        }
        if (dto.getAllowLikes() != null) {
            entity.setAllowLikes(dto.getAllowLikes());
        }
        if (dto.getIsFeatured() != null) {
            entity.setIsFeatured(dto.getIsFeatured());
        }
        if (dto.getIsPinned() != null) {
            entity.setIsPinned(dto.getIsPinned());
        }
        entity.setPinExpiresAt(dto.getPinExpiresAt());

        // SEO
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setTags(dto.getTags());
        entity.setHashtags(dto.getHashtags());

        // External links
        entity.setExternalUrl(dto.getExternalUrl());
        entity.setCallToAction(dto.getCallToAction());
        entity.setCtaUrl(dto.getCtaUrl());
    }

    // ================== POST COMMENT CONVERSIONS ==================

    public PostCommentDto mapPostCommentToDto(PostComment entity) {
        if (entity == null) {
            return null;
        }

        return PostCommentDto.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .userId(entity.getUser().getId())
                .userFullName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName())
                .userProfileImage(entity.getUser().getProfileImageUrl())
                .parentCommentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .content(entity.getContent())
                .status(entity.getStatus())
                .isEdited(ConversionUtils.defaultIfNull(entity.getIsEdited(), false))
                .editedAt(entity.getEditedAt())
                .likeCount(ConversionUtils.defaultIfNull(entity.getLikeCount(), 0L))
                .replyCount(ConversionUtils.defaultIfNull(entity.getReplyCount(), 0L))
                .isModerated(ConversionUtils.defaultIfNull(entity.getIsModerated(), false))
                .isFlagged(ConversionUtils.defaultIfNull(entity.getIsFlagged(), false))
                .replies(mapPostCommentsToDto(entity.getReplies().stream().toList()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PostComment mapPostCommentCreateDtoToEntity(PostCommentCreateDto dto) {
        if (dto == null) {
            return null;
        }

        PostComment entity = new PostComment();
        entity.setContent(dto.getContent());
        entity.setStatus(CommentStatus.PUBLISHED);
        return entity;
    }

    // ================== POST LIKE CONVERSIONS ==================

    public PostLikeDto mapPostLikeToDto(PostLike entity) {
        if (entity == null) {
            return null;
        }

        return PostLikeDto.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .userId(entity.getUser().getId())
                .userFullName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName())
                .userProfileImage(entity.getUser().getProfileImageUrl())
                .reactionType(ConversionUtils.defaultIfNull(entity.getReactionType(), ReactionType.LIKE))
                .likedAt(entity.getLikedAt())
                .build();
    }

    // ================== GALLERY CONVERSIONS ==================

    public GalleryDto mapGalleryToDto(Gallery entity) {
        if (entity == null) {
            return null;
        }

        return GalleryDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .galleryType(entity.getGalleryType())
                .visibility(ConversionUtils.defaultIfNull(entity.getVisibility(), GalleryVisibility.PUBLIC))
                .coverImageUrl(entity.getCoverImageUrl())
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .allowComments(ConversionUtils.defaultIfNull(entity.getAllowComments(), true))
                .allowDownloads(ConversionUtils.defaultIfNull(entity.getAllowDownloads(), false))

                // SEO
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .tags(entity.getTags())

                // Stats
                .itemCount(ConversionUtils.defaultIfNull(entity.getItemCount(), 0L))
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .downloadCount(ConversionUtils.defaultIfNull(entity.getDownloadCount(), 0L))
                .totalSizeBytes(ConversionUtils.defaultIfNull(entity.getTotalSizeBytes(), 0L))

                // Relationships
                .brand(institutionConverterService.mapBrandToSummaryDto(entity.getBrand()))
                .campus(institutionConverterService.mapCampusToSummaryDto(entity.getCampus()))
                .school(institutionConverterService.mapSchoolToSummaryDto(entity.getSchool()))
                .createdByUser(userConverterService.mapUserToSummaryDto(entity.getCreatedByUser()))
                .items(mapGalleryItemsToDto(entity.getItems().stream().toList()))

                // Base entity fields
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public GallerySummaryDto mapGalleryToSummaryDto(Gallery entity) {
        if (entity == null) {
            return null;
        }

        String institutionName = null;
        if (entity.getSchool() != null) {
            institutionName = entity.getSchool().getName();
        } else if (entity.getCampus() != null) {
            institutionName = entity.getCampus().getName();
        } else if (entity.getBrand() != null) {
            institutionName = entity.getBrand().getName();
        }

        return GallerySummaryDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .galleryType(entity.getGalleryType())
                .coverImageUrl(entity.getCoverImageUrl())
                .itemCount(ConversionUtils.defaultIfNull(entity.getItemCount(), 0L))
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .institutionName(institutionName)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Gallery mapGalleryCreateDtoToEntity(GalleryCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Gallery entity = new Gallery();
        updateGalleryEntityFromCreateDto(dto, entity);
        return entity;
    }

    public void updateGalleryEntityFromCreateDto(GalleryCreateDto dto, Gallery entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setGalleryType(dto.getGalleryType());
        entity.setVisibility(ConversionUtils.defaultIfNull(dto.getVisibility(), GalleryVisibility.PUBLIC));
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setIsFeatured(ConversionUtils.defaultIfNull(dto.getIsFeatured(), false));
        entity.setAllowComments(ConversionUtils.defaultIfNull(dto.getAllowComments(), true));
        entity.setAllowDownloads(ConversionUtils.defaultIfNull(dto.getAllowDownloads(), false));

        // Generate slug if title provided
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        }

        // SEO
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setTags(dto.getTags());
    }

    public void updateGalleryEntityFromUpdateDto(GalleryUpdateDto dto, Gallery entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
            entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        }

        entity.setDescription(dto.getDescription());

        if (dto.getGalleryType() != null) {
            entity.setGalleryType(dto.getGalleryType());
        }
        if (dto.getVisibility() != null) {
            entity.setVisibility(dto.getVisibility());
        }

        entity.setCoverImageUrl(dto.getCoverImageUrl());

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsFeatured() != null) {
            entity.setIsFeatured(dto.getIsFeatured());
        }
        if (dto.getAllowComments() != null) {
            entity.setAllowComments(dto.getAllowComments());
        }
        if (dto.getAllowDownloads() != null) {
            entity.setAllowDownloads(dto.getAllowDownloads());
        }

        // SEO
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setTags(dto.getTags());
    }

    // ================== GALLERY ITEM CONVERSIONS ==================

    public GalleryItemDto mapGalleryItemToDto(GalleryItem entity) {
        if (entity == null) {
            return null;
        }

        return GalleryItemDto.builder()
                .id(entity.getId())
                .galleryId(entity.getGallery().getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .altText(entity.getAltText())
                .itemType(entity.getItemType())
                .fileUrl(entity.getFileUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .fileName(entity.getFileName())
                .originalFileName(entity.getOriginalFileName())
                .fileSizeBytes(entity.getFileSizeBytes())
                .mimeType(entity.getMimeType())

                // Image specific
                .width(entity.getWidth())
                .height(entity.getHeight())
                .aspectRatio(entity.getAspectRatio())

                // Video specific
                .durationSeconds(entity.getDurationSeconds())
                .videoFormat(entity.getVideoFormat())
                .videoCodec(entity.getVideoCodec())
                .bitrate(entity.getBitrate())
                .frameRate(entity.getFrameRate())

                // Camera/Device information
                .cameraMake(entity.getCameraMake())
                .cameraModel(entity.getCameraModel())
                .takenAt(entity.getTakenAt())

                // Location
                .locationName(entity.getLocationName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())

                // Organization
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .isCover(ConversionUtils.defaultIfNull(entity.getIsCover(), false))
                .tags(entity.getTags())
                .colorPalette(entity.getColorPalette())

                // Stats
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .downloadCount(ConversionUtils.defaultIfNull(entity.getDownloadCount(), 0L))
                .likeCount(ConversionUtils.defaultIfNull(entity.getLikeCount(), 0L))

                // Processing
                .processingStatus(ConversionUtils.defaultIfNull(entity.getProcessingStatus(), ProcessingStatus.COMPLETED))
                .processingError(entity.getProcessingError())
                .processedAt(entity.getProcessedAt())

                // Moderation
                .isModerated(ConversionUtils.defaultIfNull(entity.getIsModerated(), false))
                .moderationScore(entity.getModerationScore())
                .moderationLabels(entity.getModerationLabels())
                .isFlagged(ConversionUtils.defaultIfNull(entity.getIsFlagged(), false))
                .flagReason(entity.getFlagReason())

                // Relationships
                .uploadedByUser(userConverterService.mapUserToSummaryDto(entity.getUploadedByUser()))

                // Base entity fields
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public GalleryItem mapGalleryItemCreateDtoToEntity(GalleryItemCreateDto dto) {
        if (dto == null) {
            return null;
        }

        GalleryItem entity = new GalleryItem();
        updateGalleryItemEntityFromCreateDto(dto, entity);
        return entity;
    }


    public PostItemDto mapPostItemToDto(PostItem entity) {
        if (entity == null) {
            return null;
        }

        return PostItemDto.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .altText(entity.getAltText())
                .itemType(entity.getItemType())
                .fileUrl(entity.getFileUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .fileName(entity.getFileName())
                .originalFileName(entity.getOriginalFileName())
                .fileSizeBytes(entity.getFileSizeBytes())
                .mimeType(entity.getMimeType())

                // Image specific
                .width(entity.getWidth())
                .height(entity.getHeight())
                .aspectRatio(entity.getAspectRatio())

                // Video specific
                .durationSeconds(entity.getDurationSeconds())
                .videoFormat(entity.getVideoFormat())
                .videoCodec(entity.getVideoCodec())
                .bitrate(entity.getBitrate())
                .frameRate(entity.getFrameRate())

                // Camera/Device information
                .cameraMake(entity.getCameraMake())
                .cameraModel(entity.getCameraModel())
                .takenAt(entity.getTakenAt())

                // Location
                .locationName(entity.getLocationName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())

                // Organization
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .isFeatured(ConversionUtils.defaultIfNull(entity.getIsFeatured(), false))
                .isCover(ConversionUtils.defaultIfNull(entity.getIsCover(), false))
                .tags(entity.getTags())
                .colorPalette(entity.getColorPalette())

                // Stats
                .viewCount(ConversionUtils.defaultIfNull(entity.getViewCount(), 0L))
                .downloadCount(ConversionUtils.defaultIfNull(entity.getDownloadCount(), 0L))
                .likeCount(ConversionUtils.defaultIfNull(entity.getLikeCount(), 0L))

                // Processing
                .processingStatus(ConversionUtils.defaultIfNull(entity.getProcessingStatus(), ProcessingStatus.COMPLETED))
                .processingError(entity.getProcessingError())
                .processedAt(entity.getProcessedAt())

                // Moderation
                .isModerated(ConversionUtils.defaultIfNull(entity.getIsModerated(), false))
                .moderationScore(entity.getModerationScore())
                .moderationLabels(entity.getModerationLabels())
                .isFlagged(ConversionUtils.defaultIfNull(entity.getIsFlagged(), false))
                .flagReason(entity.getFlagReason())

                // Relationships
                .uploadedByUser(userConverterService.mapUserToSummaryDto(entity.getUploadedByUser()))

                // Base entity fields
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }


    public void updateGalleryItemEntityFromCreateDto(GalleryItemCreateDto dto, GalleryItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setAltText(dto.getAltText());
        entity.setItemType(dto.getItemType());
        entity.setFileUrl(dto.getFileUrl());
        entity.setThumbnailUrl(dto.getThumbnailUrl());
        entity.setFileName(dto.getFileName());
        entity.setOriginalFileName(dto.getOriginalFileName());
        entity.setFileSizeBytes(dto.getFileSizeBytes());
        entity.setMimeType(dto.getMimeType());

        // Image/Video specific
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setVideoFormat(dto.getVideoFormat());

        // Camera/Device information
        entity.setCameraMake(dto.getCameraMake());
        entity.setCameraModel(dto.getCameraModel());
        entity.setTakenAt(dto.getTakenAt());

        // Location
        entity.setLocationName(dto.getLocationName());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());

        // Organization
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setIsFeatured(ConversionUtils.defaultIfNull(dto.getIsFeatured(), false));
        entity.setIsCover(ConversionUtils.defaultIfNull(dto.getIsCover(), false));
        entity.setTags(dto.getTags());

        // Set initial processing status
        entity.setProcessingStatus(ProcessingStatus.PENDING);
    }

    public void updateGalleryItemEntityFromUpdateDto(GalleryItemUpdateDto dto, GalleryItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setAltText(dto.getAltText());

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsFeatured() != null) {
            entity.setIsFeatured(dto.getIsFeatured());
        }
        if (dto.getIsCover() != null) {
            entity.setIsCover(dto.getIsCover());
        }

        entity.setTags(dto.getTags());

        // Location
        entity.setLocationName(dto.getLocationName());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }

    // ================== MESSAGE CONVERSIONS ==================

    public MessageDto mapMessageToDto(Message entity) {
        if (entity == null) {
            return null;
        }

        return MessageDto.builder()
                .id(entity.getId())
                .senderName(entity.getSenderName())
                .senderEmail(entity.getSenderEmail())
                .senderPhone(entity.getSenderPhone())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .messageType(entity.getMessageType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .referenceNumber(entity.getReferenceNumber())

                // Student information
                .studentName(entity.getStudentName())
                .studentAge(entity.getStudentAge())
                .gradeInterested(entity.getGradeInterested())
                .enrollmentYear(entity.getEnrollmentYear())

                // Contact preferences
                .preferredContactMethod(entity.getPreferredContactMethod())
                .preferredContactTime(entity.getPreferredContactTime())
                .requestCallback(ConversionUtils.defaultIfNull(entity.getRequestCallback(), false))
                .requestAppointment(ConversionUtils.defaultIfNull(entity.getRequestAppointment(), false))

                // Message handling
                .readAt(entity.getReadAt())
                .readBy(userConverterService.mapUserToSummaryDto(getUserById(entity.getReadBy())))
                .firstResponseAt(entity.getFirstResponseAt())
                .lastResponseAt(entity.getLastResponseAt())
                .resolvedAt(entity.getResolvedAt())
                .resolvedBy(userConverterService.mapUserToSummaryDto(getUserById(entity.getResolvedBy())))
                .responseTimeHours(entity.getResponseTimeHours())
                .resolutionTimeHours(entity.getResolutionTimeHours())

                // Follow-up
                .followUpRequired(ConversionUtils.defaultIfNull(entity.getFollowUpRequired(), false))
                .followUpDate(entity.getFollowUpDate())
                .followUpNotes(entity.getFollowUpNotes())

                // Internal notes
                .internalNotes(entity.getInternalNotes())
                .tags(entity.getTags())

                // Technical information
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .sourcePage(entity.getSourcePage())
                .utmSource(entity.getUtmSource())
                .utmMedium(entity.getUtmMedium())
                .utmCampaign(entity.getUtmCampaign())

                // Attachments
                .hasAttachments(ConversionUtils.defaultIfNull(entity.getHasAttachments(), false))
                .attachments(entity.getAttachments())

                // Satisfaction
                .satisfactionRating(entity.getSatisfactionRating())
                .satisfactionFeedback(entity.getSatisfactionFeedback())
                .satisfactionDate(entity.getSatisfactionDate())

                // Relationships
                .school(institutionConverterService.mapSchoolToSummaryDto(entity.getSchool()))
                .senderUser(userConverterService.mapUserToSummaryDto(entity.getSenderUser()))
                .assignedToUser(userConverterService.mapUserToSummaryDto(entity.getAssignedToUser()))

                // Base entity fields
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MessageSummaryDto mapMessageToSummaryDto(Message entity) {
        if (entity == null) {
            return null;
        }

        return MessageSummaryDto.builder()
                .id(entity.getId())
                .senderName(entity.getSenderName())
                .senderEmail(entity.getSenderEmail())
                .subject(entity.getSubject())
                .messageType(entity.getMessageType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .referenceNumber(entity.getReferenceNumber())
                .hasAttachments(ConversionUtils.defaultIfNull(entity.getHasAttachments(), false))
                .followUpRequired(ConversionUtils.defaultIfNull(entity.getFollowUpRequired(), false))
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .assignedToUserName(entity.getAssignedToUser() != null ?
                        entity.getAssignedToUser().getFirstName() + " " + entity.getAssignedToUser().getLastName() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Message mapMessageCreateDtoToEntity(MessageCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Message entity = new Message();
        updateMessageEntityFromCreateDto(dto, entity);
        return entity;
    }

    public void updateMessageEntityFromCreateDto(MessageCreateDto dto, Message entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setSenderName(dto.getSenderName());
        entity.setSenderEmail(dto.getSenderEmail());
        entity.setSenderPhone(dto.getSenderPhone());
        entity.setSubject(dto.getSubject());
        entity.setContent(dto.getContent());
        entity.setMessageType(ConversionUtils.defaultIfNull(dto.getMessageType(), MessageType.GENERAL_INQUIRY));
        entity.setPriority(ConversionUtils.defaultIfNull(dto.getPriority(), MessagePriority.NORMAL));
        entity.setStatus(MessageStatus.NEW);

        // Generate reference number
        entity.setReferenceNumber(generateMessageReferenceNumber());

        // Student information
        entity.setStudentName(dto.getStudentName());
        entity.setStudentAge(dto.getStudentAge());
        entity.setGradeInterested(dto.getGradeInterested());
        entity.setEnrollmentYear(dto.getEnrollmentYear());

        // Contact preferences
        entity.setPreferredContactMethod(dto.getPreferredContactMethod());
        entity.setPreferredContactTime(dto.getPreferredContactTime());
        entity.setRequestCallback(ConversionUtils.defaultIfNull(dto.getRequestCallback(), false));
        entity.setRequestAppointment(ConversionUtils.defaultIfNull(dto.getRequestAppointment(), false));

        // Technical information
        entity.setIpAddress(dto.getIpAddress());
        entity.setUserAgent(dto.getUserAgent());
        entity.setSourcePage(dto.getSourcePage());
        entity.setUtmSource(dto.getUtmSource());
        entity.setUtmMedium(dto.getUtmMedium());
        entity.setUtmCampaign(dto.getUtmCampaign());

        // Attachments
        entity.setAttachments(dto.getAttachments());
        entity.setHasAttachments(StringUtils.hasText(dto.getAttachments()));
    }

    public void updateMessageEntityFromUpdateDto(MessageUpdateDto dto, Message entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }

        entity.setInternalNotes(dto.getInternalNotes());
        entity.setTags(dto.getTags());

        if (dto.getFollowUpRequired() != null) {
            entity.setFollowUpRequired(dto.getFollowUpRequired());
        }
        entity.setFollowUpDate(dto.getFollowUpDate());
        entity.setFollowUpNotes(dto.getFollowUpNotes());
    }

    // ================== FILE UPLOAD CONVERSIONS ==================

    public FileUploadDto mapFileUploadToDto(String fileName, String originalFileName, String fileUrl,
                                            String thumbnailUrl, Long fileSizeBytes, String mimeType,
                                            MediaType mediaType, Integer width, Integer height,
                                            Integer durationSeconds, String uploadId, boolean isProcessed,
                                            String processingError) {

        return FileUploadDto.builder()
                .fileName(fileName)
                .originalFileName(originalFileName)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileSizeBytes(fileSizeBytes)
                .mimeType(mimeType)
                .mediaType(mediaType)
                .width(width)
                .height(height)
                .durationSeconds(durationSeconds)
                .uploadId(uploadId)
                .isProcessed(isProcessed)
                .processingError(processingError)
                .build();
    }

    // ================== SOCIAL MEDIA ANALYTICS CONVERSIONS ==================

    public SocialMediaAnalyticsDto mapSocialMediaAnalytics(Long schoolId, String schoolName, LocalDate analyticsDate,
                                                           Long totalPosts, Long publishedPosts, Long scheduledPosts, Long draftPosts,
                                                           Long totalLikes, Long totalComments, Long totalShares, Long totalViews,
                                                           Double engagementRate, Long newFollowers, Long newLikes, Long newComments,
                                                           PostSummaryDto topPost, List<PostSummaryDto> trendingPosts,
                                                           Long totalGalleries, Long totalGalleryItems, Long galleryViews, Long galleryDownloads) {

        return SocialMediaAnalyticsDto.builder()
                .schoolId(schoolId)
                .schoolName(schoolName)
                .analyticsDate(analyticsDate)
                .totalPosts(ConversionUtils.defaultIfNull(totalPosts, 0L))
                .publishedPosts(ConversionUtils.defaultIfNull(publishedPosts, 0L))
                .scheduledPosts(ConversionUtils.defaultIfNull(scheduledPosts, 0L))
                .draftPosts(ConversionUtils.defaultIfNull(draftPosts, 0L))
                .totalLikes(ConversionUtils.defaultIfNull(totalLikes, 0L))
                .totalComments(ConversionUtils.defaultIfNull(totalComments, 0L))
                .totalShares(ConversionUtils.defaultIfNull(totalShares, 0L))
                .totalViews(ConversionUtils.defaultIfNull(totalViews, 0L))
                .engagementRate(ConversionUtils.defaultIfNull(engagementRate, 0.0))
                .newFollowers(ConversionUtils.defaultIfNull(newFollowers, 0L))
                .newLikes(ConversionUtils.defaultIfNull(newLikes, 0L))
                .newComments(ConversionUtils.defaultIfNull(newComments, 0L))
                .topPost(topPost)
                .trendingPosts(ConversionUtils.defaultIfNull(trendingPosts, new ArrayList<>()))
                .totalGalleries(ConversionUtils.defaultIfNull(totalGalleries, 0L))
                .totalGalleryItems(ConversionUtils.defaultIfNull(totalGalleryItems, 0L))
                .galleryViews(ConversionUtils.defaultIfNull(galleryViews, 0L))
                .galleryDownloads(ConversionUtils.defaultIfNull(galleryDownloads, 0L))
                .build();
    }

    // ================== CONTENT MODERATION CONVERSIONS ==================

    public ContentModerationDto mapContentModerationDto(Long contentId, String contentType, String moderationAction,
                                                        String moderationReason, String moderatorNotes,
                                                        Double moderationScore, String moderationLabels) {

        return ContentModerationDto.builder()
                .contentId(contentId)
                .contentType(contentType)
                .moderationAction(moderationAction)
                .moderationReason(moderationReason)
                .moderatorNotes(moderatorNotes)
                .moderationScore(moderationScore)
                .moderationLabels(moderationLabels)
                .build();
    }

    // ================== BULK OPERATIONS ==================

    public BulkContentOperationResultDto mapBulkOperationResult(Boolean success, Integer totalRecords,
                                                                Integer successfulOperations, Integer failedOperations,
                                                                List<String> errors, List<String> warnings,
                                                                String operationId, LocalDateTime operationDate) {

        return BulkContentOperationResultDto.builder()
                .success(ConversionUtils.defaultIfNull(success, false))
                .totalRecords(ConversionUtils.defaultIfNull(totalRecords, 0))
                .successfulOperations(ConversionUtils.defaultIfNull(successfulOperations, 0))
                .failedOperations(ConversionUtils.defaultIfNull(failedOperations, 0))
                .errors(ConversionUtils.defaultIfNull(errors, new ArrayList<>()))
                .warnings(ConversionUtils.defaultIfNull(warnings, new ArrayList<>()))
                .operationId(operationId)
                .operationDate(ConversionUtils.defaultIfNull(operationDate, LocalDateTime.now()))
                .build();
    }

    // ================== CONTENT EXPORT ==================

    public ContentExportDto mapContentExportDto(String exportType, Long schoolId, LocalDate startDate, LocalDate endDate,
                                                String format, Boolean includeMedia, Boolean includeComments,
                                                Boolean includeAnalytics, String exportUrl, String exportStatus,
                                                LocalDateTime exportRequestDate, LocalDateTime exportCompletedDate) {

        return ContentExportDto.builder()
                .exportType(exportType)
                .schoolId(schoolId)
                .startDate(startDate)
                .endDate(endDate)
                .format(format)
                .includeMedia(ConversionUtils.defaultIfNull(includeMedia, false))
                .includeComments(ConversionUtils.defaultIfNull(includeComments, false))
                .includeAnalytics(ConversionUtils.defaultIfNull(includeAnalytics, false))
                .exportUrl(exportUrl)
                .exportStatus(exportStatus)
                .exportRequestDate(exportRequestDate)
                .exportCompletedDate(exportCompletedDate)
                .build();
    }

    // ================== COLLECTION MAPPERS ==================

    public List<PostDto> mapPostsToDto(List<Post> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapPostToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PostSummaryDto> mapPostsToSummaryDto(List<Post> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapPostToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PostCommentDto> mapPostCommentsToDto(List<PostComment> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapPostCommentToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PostLikeDto> mapPostLikesToDto(List<PostLike> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapPostLikeToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GalleryDto> mapGalleriesToDto(List<Gallery> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapGalleryToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GallerySummaryDto> mapGalleriesToSummaryDto(List<Gallery> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapGalleryToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GalleryItemDto> mapGalleryItemsToDto(List<GalleryItem> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapGalleryItemToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public List<PostItemDto> mapPostItemsToDto(List<PostItem> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapPostItemToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<MessageDto> mapMessagesToDto(List<Message> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapMessageToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<MessageSummaryDto> mapMessagesToSummaryDto(List<Message> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapMessageToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== PRIVATE HELPER METHODS ==================

    private String generateMessageReferenceNumber() {
        // Generate unique reference number: MSG-YYYYMMDD-HHMMSS-XXXXX
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String random = String.format("%05d", (int) (Math.random() * 99999));
        return "MSG-" + timestamp + "-" + random;
    }

    private User getUserById(Long userId) {
        // This method should be implemented to fetch user by ID
        // For now, returning null to avoid circular dependency
        // In real implementation, this should use UserService or Repository
        return null;
    }

    // ================== JSON UTILITY METHODS ==================

    private List<String> parseJsonToStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON to string list: {}", json, e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> parseJsonToMap(String json) {
        if (!StringUtils.hasText(json)) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON to map: {}", json, e);
            return new HashMap<>();
        }
    }

    private String convertListToJson(List<String> list) {
        if (ConversionUtils.isEmpty(list)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert list to JSON: {}", list, e);
            return null;
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert map to JSON: {}", map, e);
            return null;
        }
    }
}