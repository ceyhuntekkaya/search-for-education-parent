package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.content.*;
import com.genixo.education.search.dto.user.UserSummaryDto;
import com.genixo.education.search.entity.content.*;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.content.*;
import com.genixo.education.search.repository.insitution.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.ContentConverterService;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final GalleryRepository galleryRepository;
    private final GalleryItemRepository galleryItemRepository;
    private final MessageRepository messageRepository;
    private final SchoolRepository schoolRepository;
    private final CampusRepository campusRepository;
    private final BrandRepository brandRepository;
    private final ContentConverterService converterService;
    private final JwtService jwtService;
    private final UserAccessValidator userAccessValidator;
    private final SlugGeneratorService slugGeneratorService;
    private final PostItemRepository postItemRepository;

    // ================================ POST OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"posts", "post_summaries"}, allEntries = true)
    public PostDto createPost(PostCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new post for school ID: {}", createDto.getSchoolId());

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + createDto.getSchoolId()));

        userAccessValidator.validateUserCanAccessSchool(user, school.getId());

        // Check subscription limits ceyhun
        //userAccessValidator.validateSchoolPostLimits(school);

        String slug = slugGeneratorService.generateUniqueSlug(createDto.getTitle(), "post");
        if (postRepository.existsBySlug(slug)) {
            slug = slugGeneratorService.generateUniqueSlug(createDto.getTitle() + "-" + System.currentTimeMillis(), "post");
        }

        Post post = new Post();
        post.setSchool(school);
        post.setAuthorUser(user);
        post.setTitle(createDto.getTitle());
        post.setContent(createDto.getContent());
        post.setPostType(createDto.getPostType());
        post.setStatus(createDto.getStatus() != null ? createDto.getStatus() : PostStatus.DRAFT);
        post.setScheduledAt(createDto.getScheduledAt());
        post.setExpiresAt(createDto.getExpiresAt());
        post.setFeaturedImageUrl(createDto.getFeaturedImageUrl());
        post.setVideoUrl(createDto.getVideoUrl());
        post.setVideoThumbnailUrl(createDto.getVideoThumbnailUrl());
        post.setVideoDurationSeconds(createDto.getVideoDurationSeconds());
        post.setMediaAttachments(createDto.getMediaAttachments());
        post.setAllowComments(createDto.getAllowComments() != null ? createDto.getAllowComments() : true);
        post.setAllowLikes(createDto.getAllowLikes() != null ? createDto.getAllowLikes() : true);
        post.setIsFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false);
        post.setIsPinned(createDto.getIsPinned() != null ? createDto.getIsPinned() : false);
        post.setPinExpiresAt(createDto.getPinExpiresAt());
        post.setSlug(slug);
        post.setMetaTitle(createDto.getMetaTitle());
        post.setMetaDescription(createDto.getMetaDescription());
        post.setTags(createDto.getTags());
        post.setHashtags(createDto.getHashtags());
        post.setExternalUrl(createDto.getExternalUrl());
        post.setCallToAction(createDto.getCallToAction());
        post.setCtaUrl(createDto.getCtaUrl());
        post.setLocationName(createDto.getLocationName());
        post.setLatitude(createDto.getLatitude());
        post.setLongitude(createDto.getLongitude());
        post.setCreatedBy(user.getId());

        // Set published date if status is published
        if (post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post = postRepository.saveAndFlush(post);
        log.info("Post created successfully with ID: {}", post.getId());

        Set<PostItem> baseItemSet = new HashSet<>();
        post.setItems(baseItemSet);

        List<PostItemCreateDto> items = createDto.getItems();
        for (PostItemCreateDto item : items) {
            item.setPostId(post.getId());
            PostItem galleryItem = createPostItem(post, item, request);
            post.getItems().add(galleryItem);
        }
        post = postRepository.saveAndFlush(post);
        return converterService.mapPostToDto(post);
    }

    @Cacheable(value = "posts", key = "#id")
    public PostDto getPostById(Long id, HttpServletRequest request) {
        log.info("Fetching post with ID: {}", id);

        User user = jwtService.getUser(request);
        Post post = postRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        userAccessValidator.validateUserCanAccessSchool(user, post.getSchool().getId());

        return converterService.mapPostToDto(post);
    }

    public PostDto getPublicPostBySlug(String slug) {
        log.info("Fetching public post with slug: {}", slug);

        Post post = postRepository.findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found or not published"));

        // Check if school is subscribed
        if (!post.getSchool().getCampus().getIsSubscribed()) {
            throw new ResourceNotFoundException("Post not available");
        }

        // Increment view count
        postRepository.incrementViewCount(post.getId());

        return converterService.mapPostToDto(post);
    }

    @Transactional
    @CacheEvict(value = {"posts", "post_summaries"}, allEntries = true)
    public PostDto updatePost(Long id, PostUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating post with ID: {}", id);

        User user = jwtService.getUser(request);
        Post post = postRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        userAccessValidator.validateUserCanAccessSchool(user, post.getSchool().getId());

        post.setTitle(updateDto.getTitle());
        post.setContent(updateDto.getContent());
        post.setStatus(updateDto.getStatus());
        post.setScheduledAt(updateDto.getScheduledAt());
        post.setExpiresAt(updateDto.getExpiresAt());
        post.setFeaturedImageUrl(updateDto.getFeaturedImageUrl());
        post.setVideoUrl(updateDto.getVideoUrl());
        post.setVideoThumbnailUrl(updateDto.getVideoThumbnailUrl());
        post.setMediaAttachments(updateDto.getMediaAttachments());
        post.setAllowComments(updateDto.getAllowComments());
        post.setAllowLikes(updateDto.getAllowLikes());
        post.setIsFeatured(updateDto.getIsFeatured());
        post.setIsPinned(updateDto.getIsPinned());
        post.setPinExpiresAt(updateDto.getPinExpiresAt());
        post.setMetaTitle(updateDto.getMetaTitle());
        post.setMetaDescription(updateDto.getMetaDescription());
        post.setTags(updateDto.getTags());
        post.setHashtags(updateDto.getHashtags());
        post.setExternalUrl(updateDto.getExternalUrl());
        post.setCallToAction(updateDto.getCallToAction());
        post.setCtaUrl(updateDto.getCtaUrl());
        post.setUpdatedBy(user.getId());

        // Set published date if status changed to published
        if (post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }




        List<PostItemDto> items = updateDto.getItems();
        List<PostItem> currentItems = postItemRepository.findByPostId(post.getId());
        for(PostItem currentItem : currentItems) {
            boolean hasItem = false;
            for(PostItemDto newItem :items) {
                if(currentItem.getId().equals(newItem.getId()) && newItem.getId() != null) {
                    hasItem = true;
                }
            }
            if(!hasItem){
                post.getItems().remove(currentItem);
                postItemRepository.delete(currentItem);
            }
        }

        for (PostItemDto item : items) {
            if(item.getId() == null) {
                PostItemCreateDto createDto = new PostItemCreateDto();
                createDto.setPostId(post.getId());
                createDto.setItemType(item.getItemType());
                createDto.setFileUrl(item.getFileUrl());
                createDto.setFileName(item.getFileName());
                createDto.setSortOrder(item.getSortOrder());
                PostItem galleryItem = createPostItem(post, createDto, request);
                post.getItems().add(galleryItem);
            }
        }

       post = postRepository.saveAndFlush(post);
        log.info("Post updated successfully with ID: {}", post.getId());

        return converterService.mapPostToDto(post);
    }

    public Page<PostSummaryDto> searchPosts(PostSearchDto searchDto, HttpServletRequest request) {
        log.info("Searching posts with criteria: {}", searchDto.getSearchTerm());

        User user = jwtService.getUser(request);

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        // Filter by user access if not system admin
        List<Long> accessibleSchoolIds = userAccessValidator.getUserAccessibleSchoolIds(user);

        Page<Post> posts = postRepository.searchPosts(
                searchDto.getSearchTerm(),
                searchDto.getSchoolId(),
                accessibleSchoolIds,
                searchDto.getAuthorId(),
                searchDto.getPostType(),
                searchDto.getStatus(),
                searchDto.getIsFeatured(),
                searchDto.getIsPinned(),
                searchDto.getPublishedAfter(),
                searchDto.getPublishedBefore(),
                searchDto.getTags(),
                searchDto.getHashtags(),
                pageable
        );

        return posts.map(converterService::mapPostToSummaryDto);
    }

    // ================================ POST INTERACTION OPERATIONS ================================

    @Transactional
    public PostLikeDto togglePostLike(Long postId, ReactionType reactionType, HttpServletRequest request) {
        log.info("Toggling like for post ID: {} with reaction: {}", postId, reactionType);

        User user = jwtService.getUser(request);
        Post post = postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found or not published"));

        if (!post.getAllowLikes()) {
            throw new BusinessException("Likes are not allowed on this post");
        }

        // Check if user already liked this post
        PostLike existingLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId())
                .orElse(null);

        if (existingLike != null) {
            // Unlike - remove existing like
            postLikeRepository.delete(existingLike);
            postRepository.decrementLikeCount(postId);
            log.info("Post unliked by user: {}", user.getId());
            return null;
        } else {
            // Like - create new like
            PostLike postLike = new PostLike();
            postLike.setPost(post);
            postLike.setUser(user);
            postLike.setReactionType(reactionType);
            postLike.setLikedAt(LocalDateTime.now());
            postLike.setCreatedBy(user.getId());

            postLike = postLikeRepository.save(postLike);
            postRepository.incrementLikeCount(postId);

            log.info("Post liked by user: {}", user.getId());
            return converterService.mapPostLikeToDto(postLike);
        }
    }

    @Transactional
    public PostCommentDto createPostComment(PostCommentCreateDto createDto, HttpServletRequest request) {
        log.info("Creating comment for post ID: {}", createDto.getPostId());

        User user = jwtService.getUser(request);
        Post post = postRepository.findByIdAndStatusAndIsActiveTrue(createDto.getPostId(), PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found or not published"));

        if (!post.getAllowComments()) {
            throw new BusinessException("Comments are not allowed on this post");
        }

        PostComment parentComment = null;
        if (createDto.getParentCommentId() != null) {
            parentComment = postCommentRepository.findByIdAndIsActiveTrue(createDto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setParentComment(parentComment);
        comment.setContent(createDto.getContent());
        comment.setStatus(CommentStatus.PUBLISHED); // Could be MODERATION based on settings
        comment.setCreatedBy(user.getId());

        comment = postCommentRepository.save(comment);

        // Update comment counts
        postRepository.incrementCommentCount(createDto.getPostId());
        if (parentComment != null) {
            postCommentRepository.incrementReplyCount(parentComment.getId());
        }

        log.info("Comment created successfully with ID: {}", comment.getId());
        return converterService.mapPostCommentToDto(comment);
    }

    public Page<PostCommentDto> getPostComments(Long postId, Integer page, Integer size) {
        log.info("Fetching comments for post ID: {}", postId);

        // Verify post exists and is published
        postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found or not published"));

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<PostComment> comments = postCommentRepository.findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                postId, CommentStatus.PUBLISHED, pageable);

        return comments.map(converterService::mapPostCommentToDto);
    }

    // ================================ GALLERY OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"galleries", "gallery_summaries"}, allEntries = true)
    public GalleryDto createGallery(GalleryCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new gallery: {}", createDto.getTitle());

        User user = jwtService.getUser(request);

        // Validate at least one entity is specified
        if (createDto.getBrandId() == null && createDto.getCampusId() == null && createDto.getSchoolId() == null) {
            throw new BusinessException("Gallery must belong to a brand, campus, or school");
        }

        Brand brand = null;
        Campus campus = null;
        School school = null;

        if (createDto.getSchoolId() != null) {
            school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found"));
            userAccessValidator.validateUserCanAccessSchool(user, school.getId());
        } else if (createDto.getCampusId() != null) {
            campus = campusRepository.findByIdAndIsActiveTrue(createDto.getCampusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campus not found"));
            userAccessValidator.validateUserCanAccessCampus(user, campus.getId());
        } else if (createDto.getBrandId() != null) {
            brand = brandRepository.findByIdAndIsActiveTrue(createDto.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
            userAccessValidator.validateUserCanAccessBrand(user, brand.getId());
        }

        String slug = slugGeneratorService.generateUniqueSlug(createDto.getTitle(), "gallery");
        if (galleryRepository.existsBySlug(slug)) {
            slug = slugGeneratorService.generateUniqueSlug(createDto.getTitle() + "-" + System.currentTimeMillis(), "gallery");
        }

        Gallery gallery = new Gallery();
        gallery.setBrand(brand);
        gallery.setCampus(campus);
        gallery.setSchool(school);
        gallery.setCreatedByUser(user);
        gallery.setTitle(createDto.getTitle());
        gallery.setDescription(createDto.getDescription());
        gallery.setSlug(slug);
        gallery.setGalleryType(createDto.getGalleryType());
        gallery.setVisibility(createDto.getVisibility() != null ? createDto.getVisibility() : GalleryVisibility.PUBLIC);
        gallery.setCoverImageUrl(createDto.getCoverImageUrl());
        gallery.setSortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : 0);
        gallery.setIsFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false);
        gallery.setAllowComments(createDto.getAllowComments() != null ? createDto.getAllowComments() : true);
        gallery.setAllowDownloads(createDto.getAllowDownloads() != null ? createDto.getAllowDownloads() : false);
        gallery.setMetaTitle(createDto.getMetaTitle());
        gallery.setMetaDescription(createDto.getMetaDescription());
        gallery.setTags(createDto.getTags());
        gallery.setCreatedBy(user.getId());

        gallery = galleryRepository.saveAndFlush(gallery);
        log.info("Gallery created successfully with ID: {}", gallery.getId());
        Set<GalleryItem> baseItemSet = new HashSet<>();
        gallery.setItems(baseItemSet);


        List<GalleryItemCreateDto> items = createDto.getItems();
        for (GalleryItemCreateDto item : items) {
            item.setGalleryId(gallery.getId());
            GalleryItem galleryItem = createGalleryItem(gallery, item, request);
            gallery.getItems().add(galleryItem);
        }

        gallery = galleryRepository.saveAndFlush(gallery);
        //converterService.mapGalleryItemToDto(item);

        return converterService.mapGalleryToDto(gallery);
    }

    @Cacheable(value = "galleries", key = "#id")
    public GalleryDto getGalleryById(Long id, HttpServletRequest request) {
        log.info("Fetching gallery with ID: {}", id);

        User user = jwtService.getUser(request);
        Gallery gallery = galleryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found with ID: " + id));

        userAccessValidator.validateUserCanAccessGallery(user, gallery);

        return converterService.mapGalleryToDto(gallery);
    }

    public Page<GallerySummaryDto> searchGalleries(GallerySearchDto searchDto, HttpServletRequest request) {
        log.info("Searching galleries with criteria: {}", searchDto.getSearchTerm());

        User user = jwtService.getUser(request);

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        // Get accessible entity IDs
        List<Long> accessibleBrandIds = userAccessValidator.getUserAccessibleBrandIds(user);
        List<Long> accessibleCampusIds = userAccessValidator.getUserAccessibleCampusIds(user);
        List<Long> accessibleSchoolIds = userAccessValidator.getUserAccessibleSchoolIds(user);

        Page<Gallery> galleries = galleryRepository.searchGalleries(
                searchDto.getSearchTerm(),
                searchDto.getBrandId(),
                searchDto.getCampusId(),
                searchDto.getSchoolId(),
                accessibleBrandIds,
                accessibleCampusIds,
                accessibleSchoolIds,
                searchDto.getGalleryType(),
                searchDto.getVisibility(),
                searchDto.getIsFeatured(),
                searchDto.getTags(),
                pageable
        );

        return galleries.map(converterService::mapGalleryToSummaryDto);
    }

    // ================================ GALLERY ITEM OPERATIONS ================================

    @Transactional
    public GalleryItem createGalleryItem(Gallery gallery, GalleryItemCreateDto createDto, HttpServletRequest request) {
        log.info("Adding item to gallery ID: {}", createDto.getGalleryId());

        User user = jwtService.getUser(request);

        userAccessValidator.validateUserCanAccessGallery(user, gallery);

        GalleryItem item = new GalleryItem();
        item.setGallery(gallery);
        item.setUploadedByUser(user);
        item.setTitle(createDto.getTitle());
        item.setDescription(createDto.getDescription());
        item.setAltText(createDto.getAltText());
        item.setItemType(createDto.getItemType());
        item.setFileUrl(createDto.getFileUrl());
        item.setThumbnailUrl(createDto.getThumbnailUrl());
        item.setFileName(createDto.getFileName());
        item.setOriginalFileName(createDto.getOriginalFileName());
        item.setFileSizeBytes(createDto.getFileSizeBytes());
        item.setMimeType(createDto.getMimeType());
        item.setWidth(createDto.getWidth());
        item.setHeight(createDto.getHeight());
        item.setDurationSeconds(createDto.getDurationSeconds());
        item.setVideoFormat(createDto.getVideoFormat());
        item.setCameraMake(createDto.getCameraMake());
        item.setCameraModel(createDto.getCameraModel());
        item.setTakenAt(createDto.getTakenAt());
        item.setLocationName(createDto.getLocationName());
        item.setLatitude(createDto.getLatitude());
        item.setLongitude(createDto.getLongitude());
        item.setSortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : 0);
        item.setIsFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false);
        item.setIsCover(createDto.getIsCover() != null ? createDto.getIsCover() : false);
        item.setTags(createDto.getTags());
        item.setProcessingStatus(ProcessingStatus.COMPLETED); // Assume already processed
        item.setCreatedBy(user.getId());

        // Calculate aspect ratio if dimensions available
        if (item.getWidth() != null && item.getHeight() != null && item.getHeight() > 0) {
            double ratio = (double) item.getWidth() / item.getHeight();
            item.setAspectRatio(String.format("%.2f", ratio));
        }

        item = galleryItemRepository.saveAndFlush(item);
        log.info("Gallery item created successfully with ID: {}", item.getId());
        return item;
    }







    @Transactional
    public PostItem createPostItem(Post post, PostItemCreateDto createDto, HttpServletRequest request) {
        log.info("Adding item to post ID: {}", createDto.getPostId());

        User user = jwtService.getUser(request);


        PostItem item = new PostItem();
        item.setPost(post);
        item.setUploadedByUser(user);
        item.setTitle(createDto.getTitle());
        item.setDescription(createDto.getDescription());
        item.setAltText(createDto.getAltText());
        item.setItemType(createDto.getItemType());
        item.setFileUrl(createDto.getFileUrl());
        item.setThumbnailUrl(createDto.getThumbnailUrl());
        item.setFileName(createDto.getFileName());
        item.setOriginalFileName(createDto.getOriginalFileName());
        item.setFileSizeBytes(createDto.getFileSizeBytes());
        item.setMimeType(createDto.getMimeType());
        item.setWidth(createDto.getWidth());
        item.setHeight(createDto.getHeight());
        item.setDurationSeconds(createDto.getDurationSeconds());
        item.setVideoFormat(createDto.getVideoFormat());
        item.setCameraMake(createDto.getCameraMake());
        item.setCameraModel(createDto.getCameraModel());
        item.setTakenAt(createDto.getTakenAt());
        item.setLocationName(createDto.getLocationName());
        item.setLatitude(createDto.getLatitude());
        item.setLongitude(createDto.getLongitude());
        item.setSortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : 0);
        item.setIsFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false);
        item.setIsCover(createDto.getIsCover() != null ? createDto.getIsCover() : false);
        item.setTags(createDto.getTags());
        item.setProcessingStatus(ProcessingStatus.COMPLETED); // Assume already processed
        item.setCreatedBy(user.getId());

        // Calculate aspect ratio if dimensions available
        if (item.getWidth() != null && item.getHeight() != null && item.getHeight() > 0) {
            double ratio = (double) item.getWidth() / item.getHeight();
            item.setAspectRatio(String.format("%.2f", ratio));
        }

        item = postItemRepository.saveAndFlush(item);
        log.info("Gallery item created successfully with ID: {}", item.getId());
        return item;
    }

    // ================================ MESSAGE OPERATIONS ================================

    @Transactional
    public MessageDto createMessage(MessageCreateDto createDto, HttpServletRequest request) {
        log.info("Creating message for school ID: {}", createDto.getSchoolId());

        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        // Generate unique reference number
        String referenceNumber = UUID.randomUUID().toString();

        Message message = new Message();
        message.setSchool(school);
        message.setSenderName(createDto.getSenderName());
        message.setSenderEmail(createDto.getSenderEmail());
        message.setSenderPhone(createDto.getSenderPhone());
        message.setSubject(createDto.getSubject());
        message.setContent(createDto.getContent());
        message.setMessageType(createDto.getMessageType());
        message.setPriority(createDto.getPriority() != null ? createDto.getPriority() : MessagePriority.NORMAL);
        message.setStatus(MessageStatus.NEW);
        message.setReferenceNumber(referenceNumber);
        message.setStudentName(createDto.getStudentName());
        message.setStudentAge(createDto.getStudentAge());
        message.setGradeInterested(createDto.getGradeInterested());
        message.setEnrollmentYear(createDto.getEnrollmentYear());
        message.setPreferredContactMethod(createDto.getPreferredContactMethod());
        message.setPreferredContactTime(createDto.getPreferredContactTime());
        message.setRequestCallback(createDto.getRequestCallback() != null ? createDto.getRequestCallback() : false);
        message.setRequestAppointment(createDto.getRequestAppointment() != null ? createDto.getRequestAppointment() : false);
        message.setIpAddress(createDto.getIpAddress());
        message.setUserAgent(createDto.getUserAgent());
        message.setSourcePage(createDto.getSourcePage());
        message.setUtmSource(createDto.getUtmSource());
        message.setUtmMedium(createDto.getUtmMedium());
        message.setUtmCampaign(createDto.getUtmCampaign());
        message.setAttachments(createDto.getAttachments());
        message.setHasAttachments(StringUtils.hasText(createDto.getAttachments()));

        // Try to get user from request if authenticated
        try {
            User user = jwtService.getUser(request);
            message.setSenderUser(user);
            message.setCreatedBy(user.getId());
        } catch (Exception e) {
            // Anonymous message - no user associated
            log.debug("Anonymous message created");
        }

        message = messageRepository.save(message);
        log.info("Message created successfully with reference: {}", referenceNumber);

        return converterService.mapMessageToDto(message);
    }

    public MessageDto getMessageById(Long id, HttpServletRequest request) {
        log.info("Fetching message with ID: {}", id);

        User user = jwtService.getUser(request);
        Message message = messageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + id));

        userAccessValidator.validateUserCanAccessSchool(user, message.getSchool().getId());

        // Mark as read if not already read
        if (message.getReadAt() == null) {
            markMessageAsRead(message, user);
        }

        return converterService.mapMessageToDto(message);
    }


    @Transactional
    public void markMessageAsRead(Message message, User user) {
        log.info("Message mark as read with ID: {}", message.getId());
        message.setReadBy(user.getId());
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Transactional
    public MessageDto updateMessage(Long id, MessageUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating message with ID: {}", id);

        User user = jwtService.getUser(request);
        Message message = messageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + id));

        userAccessValidator.validateUserCanAccessSchool(user, message.getSchool().getId());

        message.setStatus(updateDto.getStatus());
        message.setPriority(updateDto.getPriority());
        message.setInternalNotes(updateDto.getInternalNotes());
        message.setTags(updateDto.getTags());
        message.setFollowUpRequired(updateDto.getFollowUpRequired());
        message.setFollowUpDate(updateDto.getFollowUpDate());
        message.setFollowUpNotes(updateDto.getFollowUpNotes());
        message.setUpdatedBy(user.getId());

        // Set assigned user if provided
        if (updateDto.getAssignedToUserId() != null) {
            // Validate assigned user has access to this school
            // This would require UserRepository to check user access
            message.setAssignedToUser(new User()); // Simplified - would need proper user lookup
        }

        // Set first response time if this is first response
        if (message.getFirstResponseAt() == null &&
                (updateDto.getStatus() == MessageStatus.RESPONDED ||
                        updateDto.getStatus() == MessageStatus.IN_PROGRESS)) {
            message.setFirstResponseAt(LocalDateTime.now());
            calculateResponseTime(message);
        }

        // Set resolved time if message is resolved
        if (updateDto.getStatus() == MessageStatus.RESOLVED && message.getResolvedAt() == null) {
            message.setResolvedAt(LocalDateTime.now());
            message.setResolvedBy(user.getId());
            calculateResolutionTime(message);
        }

        message = messageRepository.save(message);
        return converterService.mapMessageToDto(message);
    }


    public static void calculateResponseTime(Message message) {
        if (message.getFirstResponseAt() != null && message.getCreatedAt() != null) {
            Duration duration = Duration.between(message.getCreatedAt(), message.getFirstResponseAt());
            double hours = duration.toMinutes() / 60.0;
            message.setResponseTimeHours(hours);
        }
    }

    /**
     * Çözüm süresini (saat cinsinden) hesaplar.
     * Hesaplama: resolvedAt - createdAt
     */
    public static void calculateResolutionTime(Message message) {
        if (message.getResolvedAt() != null && message.getCreatedAt() != null) {
            Duration duration = Duration.between(message.getCreatedAt(), message.getResolvedAt());
            double hours = duration.toMinutes() / 60.0;
            message.setResolutionTimeHours(hours);
        }
    }

    public static Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            // default sıralama: createdAt DESC
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            // default: DESC
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, sortBy);
    }

    public List<PostSummaryDto> getPostBySchoolId(Long id) {
        log.info("Fetching post with ID: {}", id);
        List<Post> posts = postRepository.findBySchoolIdAndIsActiveTrue(id);
        return posts.stream().map(converterService::mapPostToSummaryDto).toList();
    }

    public List<MessageDto> getMessageBySchoolId(Long id, HttpServletRequest request) {
        User user = jwtService.getUser(request);
        List<Message> message = messageRepository.findBySchoolIdAndIsActiveTrueList(id);
        return converterService.mapMessagesToDto(message);

    }

    public List<GalleryDto> getGalleryBySchoolId(Long id) {

        log.info("Fetching gallery with ID: {}", id);
        List<Gallery> gallery = galleryRepository.findBySchoolIdAndIsActiveTrue(id);
        return converterService.mapGalleriesToDto(gallery);

    }

    @Transactional
    public GalleryDto updateGallery(Long id,  GalleryUpdateDto updateDto, HttpServletRequest request) {

        Gallery gallery = galleryRepository.findById(id).orElse(null);
        if(gallery == null) {
            throw new ResourceNotFoundException("Gallery not found with ID: " + id);
        }

        log.info("Updatinggallery: {}", updateDto.getTitle());

        User user = jwtService.getUser(request);

        gallery.setTitle(updateDto.getTitle());
        gallery.setDescription(updateDto.getDescription());
        gallery.setGalleryType(GalleryType.MIXED);
        gallery.setVisibility(updateDto.getVisibility() != null ? updateDto.getVisibility() : GalleryVisibility.PUBLIC);
        gallery.setCoverImageUrl(updateDto.getCoverImageUrl());
        gallery.setSortOrder(updateDto.getSortOrder() != null ? updateDto.getSortOrder() : 0);
        gallery.setIsFeatured(updateDto.getIsFeatured() != null ? updateDto.getIsFeatured() : false);
        gallery.setAllowComments(updateDto.getAllowComments() != null ? updateDto.getAllowComments() : true);
        gallery.setAllowDownloads(updateDto.getAllowDownloads() != null ? updateDto.getAllowDownloads() : false);
        gallery.setMetaTitle(updateDto.getMetaTitle());
        gallery.setMetaDescription(updateDto.getMetaDescription());
        gallery.setTags(updateDto.getTags());
        gallery.setCreatedBy(user.getId());


        log.info("Gallery updated successfully with ID: {}", gallery.getId());

        List<GalleryItemDto> items = updateDto.getItems();


        List<GalleryItem> currentItems = galleryItemRepository.findByGalleryId(gallery.getId());
        for(GalleryItem currentItem : currentItems) {
            boolean hasItem = false;
            for(GalleryItemDto newItem :items) {
                if(currentItem.getId().equals(newItem.getId()) && newItem.getId() != null) {
                    hasItem = true;
                }
            }
            if(!hasItem){
                gallery.getItems().remove(currentItem);
                galleryItemRepository.delete(currentItem);
            }
        }


        for (GalleryItemDto item : items) {
            if(item.getId() == null) {
                GalleryItemCreateDto createDto = new GalleryItemCreateDto();
                createDto.setGalleryId(gallery.getId());
                createDto.setItemType(item.getItemType());
                createDto.setFileUrl(item.getFileUrl());
                createDto.setFileName(item.getFileName());
                createDto.setSortOrder(item.getSortOrder());
                GalleryItem galleryItem = createGalleryItem(gallery, createDto, request);
                gallery.getItems().add(galleryItem);
            }
        }

        gallery = galleryRepository.save(gallery);
        return converterService.mapGalleryToDto(gallery);
    }

    public List<MessageGroupDto> getMessageForUser(Long userId) {
        List<Message> messages = messageRepository.findByUser(userId);
        List<MessageDto> messageDtos = converterService.mapMessagesToDto(messages);

        Set<UserSummaryDto> users = messageDtos.stream()
                .flatMap(msg -> Stream.of(msg.getSenderUser(), msg.getAssignedToUser()))
                .filter(Objects::nonNull)
                .filter(user -> !user.getId().equals(userId)) // Fonksiyona gelen userId'yi hariç tut
                .collect(Collectors.toSet());

        List<MessageGroupDto> messageGroupDtos = new ArrayList<>();

        for (UserSummaryDto user : users) {
            // Bu user ile olan tüm konuşmaları bul (gönderen veya alıcı olarak)
            List<MessageDto> userConversations = messageDtos.stream()
                    .filter(msg ->
                            (msg.getSenderUser() != null && msg.getSenderUser().getId().equals(user.getId())) ||
                                    (msg.getAssignedToUser() != null && msg.getAssignedToUser().getId().equals(user.getId()))
                    )
                    .sorted(Comparator.comparing(MessageDto::getCreatedAt)) // En eski en üstte, en yeni en altta
                    .collect(Collectors.toList());

            // En son mesaj tarihini bul
            LocalDateTime lastMessageDate = userConversations.stream()
                    .map(MessageDto::getCreatedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            // MessageGroupDto oluştur
            MessageGroupDto groupDto = new MessageGroupDto();
            groupDto.setConversations(userConversations);
            groupDto.setTotalConversations(userConversations.size());
            groupDto.setPersonName(user.getFullName()); // veya getFullName() varsa onu kullanın
            groupDto.setUserId(user.getId());
            groupDto.setLastMessageDate(lastMessageDate);

            messageGroupDtos.add(groupDto);
        }

        // Son olarak listeyi lastMessageDate'e göre sırala (en yeni en üstte)
        messageGroupDtos.sort(Comparator.comparing(MessageGroupDto::getLastMessageDate,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return messageGroupDtos;
    }

    public List<MessageGroupDto> getMessageForSchool(Long schoolId) {

        List<Message> messages = messageRepository.findBySchool(schoolId);
        List<MessageDto> messageDtos = converterService.mapMessagesToDto(messages);

        Set<UserSummaryDto> users = messageDtos.stream()
                .flatMap(msg -> Stream.of(msg.getSenderUser(), msg.getAssignedToUser()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<MessageGroupDto> messageGroupDtos = new ArrayList<>();

        for (UserSummaryDto user : users) {
            // Bu user ile olan tüm konuşmaları bul (gönderen veya alıcı olarak)
            List<MessageDto> userConversations = messageDtos.stream()
                    .filter(msg ->
                            (msg.getSenderUser() != null && msg.getSenderUser().getId().equals(user.getId())) ||
                                    (msg.getAssignedToUser() != null && msg.getAssignedToUser().getId().equals(user.getId()))
                    )
                    .sorted(Comparator.comparing(MessageDto::getCreatedAt)) // En eski en üstte, en yeni en altta
                    .collect(Collectors.toList());

            // En son mesaj tarihini bul
            LocalDateTime lastMessageDate = userConversations.stream()
                    .map(MessageDto::getCreatedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            // MessageGroupDto oluştur
            MessageGroupDto groupDto = new MessageGroupDto();
            groupDto.setConversations(userConversations);
            groupDto.setTotalConversations(userConversations.size());
            groupDto.setPersonName(user.getFullName()); // veya getFullName() varsa onu kullanın
            groupDto.setUserId(user.getId());
            groupDto.setLastMessageDate(lastMessageDate);

            messageGroupDtos.add(groupDto);
        }

        // Son olarak listeyi lastMessageDate'e göre sırala (en yeni en üstte)
        messageGroupDtos.sort(Comparator.comparing(MessageGroupDto::getLastMessageDate,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return messageGroupDtos;
    }

    public void makeReadMessage(Long id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
}