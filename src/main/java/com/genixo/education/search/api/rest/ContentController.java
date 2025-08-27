package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.content.*;
import com.genixo.education.search.enumaration.ReactionType;
import com.genixo.education.search.service.ContentService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Content Management", description = "APIs for managing posts, galleries, messages and other content")
public class ContentController {

    private final ContentService contentService;
    private final JwtService jwtService;

    // ================================ POST OPERATIONS ================================

    @PostMapping("/posts")
    @Operation(summary = "Create post", description = "Create a new post for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Post created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid post data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create post request for school: {}", createDto.getSchoolId());

        PostDto postDto = contentService.createPost(createDto, request);

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by ID", description = "Get post details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PostDto>> getPostById(
            @Parameter(description = "Post ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get post request: {}", id);

        PostDto postDto = contentService.getPostById(id, request);

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/public/{slug}")
    @Operation(summary = "Get public post by slug", description = "Get published post by slug (public access)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found or not published")
    })
    public ResponseEntity<ApiResponse<PostDto>> getPublicPostBySlug(
            @Parameter(description = "Post slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get public post by slug request: {}", slug);

        PostDto postDto = contentService.getPublicPostBySlug(slug);

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "Update post", description = "Update an existing post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @Parameter(description = "Post ID") @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update post request: {}", id);

        PostDto postDto = contentService.updatePost(id, updateDto, request);

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/search")
    @Operation(summary = "Search posts", description = "Search posts with advanced filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<PostSummaryDto>>> searchPosts(
            @Valid @RequestBody PostSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search posts request");

        Page<PostSummaryDto> posts = contentService.searchPosts(searchDto, request);

        ApiResponse<Page<PostSummaryDto>> response = ApiResponse.success(posts, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ POST INTERACTION OPERATIONS ================================

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "Toggle post like", description = "Like or unlike a post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post like toggled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found or not published"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Likes not allowed on this post")
    })
    public ResponseEntity<ApiResponse<PostLikeDto>> togglePostLike(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @Parameter(description = "Reaction type") @RequestParam(defaultValue = "LIKE") ReactionType reactionType,
            HttpServletRequest request) {

        log.info("Toggle like for post: {} with reaction: {}", postId, reactionType);

        PostLikeDto likeDto = contentService.togglePostLike(postId, reactionType, request);

        String message = likeDto != null ? "Post liked successfully" : "Post unliked successfully";
        ApiResponse<PostLikeDto> response = ApiResponse.success(likeDto, message);
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/comments")
    @Operation(summary = "Create post comment", description = "Add a comment to a post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found or not published"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Comments not allowed on this post")
    })
    public ResponseEntity<ApiResponse<PostCommentDto>> createPostComment(
            @Valid @RequestBody PostCommentCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create comment for post: {}", createDto.getPostId());

        PostCommentDto commentDto = contentService.createPostComment(createDto, request);

        ApiResponse<PostCommentDto> response = ApiResponse.success(commentDto, "Comment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "Get post comments", description = "Get all comments for a post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found or not published")
    })
    public ResponseEntity<ApiResponse<Page<PostCommentDto>>> getPostComments(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        log.debug("Get comments for post: {}", postId);

        Page<PostCommentDto> comments = contentService.getPostComments(postId, page, size);

        ApiResponse<Page<PostCommentDto>> response = ApiResponse.success(comments, "Comments retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ GALLERY OPERATIONS ================================

    @PostMapping("/galleries")
    @Operation(summary = "Create gallery", description = "Create a new gallery for brand, campus, or school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Gallery created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid gallery data or no entity specified"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<ApiResponse<GalleryDto>> createGallery(
            @Valid @RequestBody GalleryCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create gallery request: {}", createDto.getTitle());

        GalleryDto galleryDto = contentService.createGallery(createDto, request);

        ApiResponse<GalleryDto> response = ApiResponse.success(galleryDto, "Gallery created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/galleries/{id}")
    @Operation(summary = "Get gallery by ID", description = "Get gallery details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gallery retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gallery not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<GalleryDto>> getGalleryById(
            @Parameter(description = "Gallery ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get gallery request: {}", id);

        GalleryDto galleryDto = contentService.getGalleryById(id, request);

        ApiResponse<GalleryDto> response = ApiResponse.success(galleryDto, "Gallery retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/galleries/search")
    @Operation(summary = "Search galleries", description = "Search galleries with filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<GallerySummaryDto>>> searchGalleries(
            @Valid @RequestBody GallerySearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search galleries request");

        Page<GallerySummaryDto> galleries = contentService.searchGalleries(searchDto, request);

        ApiResponse<Page<GallerySummaryDto>> response = ApiResponse.success(galleries, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ GALLERY ITEM OPERATIONS ================================

    @PostMapping("/galleries/items")
    @Operation(summary = "Create gallery item", description = "Add a new item (photo/video) to a gallery")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Gallery item created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gallery not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid item data")
    })
    public ResponseEntity<ApiResponse<GalleryItemDto>> createGalleryItem(
            @Valid @RequestBody GalleryItemCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create gallery item for gallery: {}", createDto.getGalleryId());

        GalleryItemDto itemDto = contentService.createGalleryItem(createDto, request);

        ApiResponse<GalleryItemDto> response = ApiResponse.success(itemDto, "Gallery item created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ MESSAGE OPERATIONS ================================

    @PostMapping("/messages")
    @Operation(summary = "Create message", description = "Create a new message/inquiry to a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Message created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid message data")
    })
    public ResponseEntity<ApiResponse<MessageDto>> createMessage(
            @Valid @RequestBody MessageCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create message for school: {}", createDto.getSchoolId());

        MessageDto messageDto = contentService.createMessage(createDto, request);

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/messages/{id}")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<MessageDto>> getMessageById(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get message request: {}", id);

        MessageDto messageDto = contentService.getMessageById(id, request);

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/messages/{id}")
    @Operation(summary = "Update message", description = "Update message status and details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<ApiResponse<MessageDto>> updateMessage(
            @Parameter(description = "Message ID") @PathVariable Long id,
            @Valid @RequestBody MessageUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update message request: {}", id);

        MessageDto messageDto = contentService.updateMessage(id, updateDto, request);

        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
/*
    // ================================ CONTENT STATISTICS ================================

    @GetMapping("/posts/{postId}/statistics")
    @Operation(summary = "Get post statistics", description = "Get detailed statistics for a post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PostStatisticsDto>> getPostStatistics(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            HttpServletRequest request) {

        log.debug("Get post statistics request: {}", postId);

        // This would be implemented in the service
        PostStatisticsDto statistics = PostStatisticsDto.builder()
                .postId(postId)
                .viewCount(1250L)
                .likeCount(89L)
                .commentCount(23L)
                .shareCount(15L)
                .engagementRate(7.12)
                .averageTimeOnPage(145.5)
                .uniqueViewers(987L)
                .build();

        ApiResponse<PostStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/galleries/{galleryId}/statistics")
    @Operation(summary = "Get gallery statistics", description = "Get detailed statistics for a gallery")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gallery not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<GalleryStatisticsDto>> getGalleryStatistics(
            @Parameter(description = "Gallery ID") @PathVariable Long galleryId,
            HttpServletRequest request) {

        log.debug("Get gallery statistics request: {}", galleryId);

        // This would be implemented in the service
        GalleryStatisticsDto statistics = GalleryStatisticsDto.builder()
                .galleryId(galleryId)
                .totalItems(45L)
                .totalViews(2150L)
                .totalDownloads(234L)
                .totalSizeBytes(125000000L)
                .averageRating(4.3)
                .mostViewedItemId(123L)
                .recentViews7Days(567L)
                .build();

        ApiResponse<GalleryStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CONTENT MODERATION ================================

    @PostMapping("/posts/{postId}/moderate")
    @Operation(summary = "Moderate post", description = "Moderate post content (approve, reject, flag)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post moderated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PostDto>> moderatePost(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @Valid @RequestBody ContentModerationRequestDto moderationRequest,
            HttpServletRequest request) {

        log.info("Moderate post request: {} - action: {}", postId, moderationRequest.getAction());

        // This would be implemented in the service
        PostDto postDto = contentService.getPostById(postId, request); // Placeholder

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post moderated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments/{commentId}/moderate")
    @Operation(summary = "Moderate comment", description = "Moderate comment content (approve, reject, delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comment moderated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Comment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PostCommentDto>> moderateComment(
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            @Valid @RequestBody ContentModerationRequestDto moderationRequest,
            HttpServletRequest request) {

        log.info("Moderate comment request: {} - action: {}", commentId, moderationRequest.getAction());

        // This would be implemented in the service
        PostCommentDto commentDto = PostCommentDto.builder()
                .id(commentId)
                .content("Comment content")
                .status(com.genixo.education.search.enumaration.CommentStatus.PUBLISHED)
                .build();

        ApiResponse<PostCommentDto> response = ApiResponse.success(commentDto, "Comment moderated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CONTENT REPORTING ================================

    @PostMapping("/posts/{postId}/report")
    @Operation(summary = "Report post", description = "Report inappropriate post content")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Report submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report data")
    })
    public ResponseEntity<ApiResponse<ContentReportDto>> reportPost(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @Valid @RequestBody ContentReportRequestDto reportRequest,
            HttpServletRequest request) {

        log.info("Report post request: {} - reason: {}", postId, reportRequest.getReason());

        // This would be implemented in the service
        ContentReportDto report = ContentReportDto.builder()
                .id(System.currentTimeMillis())
                .contentType("POST")
                .contentId(postId)
                .reason(reportRequest.getReason())
                .description(reportRequest.getDescription())
                .status("PENDING")
                .reportedAt(LocalDateTime.now())
                .build();

        ApiResponse<ContentReportDto> response = ApiResponse.success(report, "Report submitted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/posts/bulk")
    @Operation(summary = "Bulk post operations", description = "Perform bulk operations on multiple posts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bulk operation data")
    })
    public ResponseEntity<ApiResponse<BulkContentResultDto>> bulkPostOperations(
            @Valid @RequestBody BulkContentOperationDto operationDto,
            HttpServletRequest request) {

        log.info("Bulk post operation request: {} on {} posts",
                operationDto.getOperation(), operationDto.getContentIds().size());

        // This would be implemented in the service
        BulkContentResultDto result = BulkContentResultDto.builder()
                .operationDate(LocalDateTime.now())
                .totalRecords(operationDto.getContentIds().size())
                .successfulOperations(operationDto.getContentIds().size())
                .failedOperations(0)
                .errors(List.of())
                .affectedContentIds(operationDto.getContentIds())
                .build();

        ApiResponse<BulkContentResultDto> response = ApiResponse.success(result, "Bulk operation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CONTENT EXPORT ================================

    @PostMapping("/export")
    @Operation(summary = "Export content", description = "Export content data in various formats")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Export request accepted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid export parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ContentExportDto>> exportContent(
            @Valid @RequestBody ContentExportRequestDto exportRequest,
            HttpServletRequest request) {

        log.info("Export content request: {} - format: {}",
                exportRequest.getContentType(), exportRequest.getFormat());

        // This would be implemented in the service
        ContentExportDto export = ContentExportDto.builder()
                .exportId("EXP_" + System.currentTimeMillis())
                .contentType(exportRequest.getContentType())
                .format(exportRequest.getFormat())
                .status("PROCESSING")
                .progress(0)
                .requestedAt(LocalDateTime.now())
                .estimatedCompletionTime(LocalDateTime.now().plusMinutes(15))
                .build();

        ApiResponse<ContentExportDto> response = ApiResponse.success(export, "Export request accepted");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // ================================ HELPER REQUEST DTOs ================================

    @Setter
    @Getter
    public static class ContentModerationRequestDto {
        private String action; // APPROVE, REJECT, FLAG, DELETE
        private String reason;
        private String notes;

    }

    @Setter
    @Getter
    public static class ContentReportRequestDto {
        private String reason; // SPAM, INAPPROPRIATE, OFFENSIVE, COPYRIGHT, OTHER
        private String description;

    }

    @Setter
    @Getter
    public static class ContentExportRequestDto {
        private String contentType; // POSTS, GALLERIES, MESSAGES
        private String format; // JSON, CSV, XML
        private List<Long> contentIds;
        private java.time.LocalDate startDate;
        private java.time.LocalDate endDate;
        private List<Long> schoolIds;

    }

    // ================================ CONTENT ANALYTICS ================================

    @GetMapping("/schools/{schoolId}/content-analytics")
    @Operation(summary = "Get school content analytics", description = "Get comprehensive content analytics for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<ContentAnalyticsDto>> getSchoolContentAnalytics(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            HttpServletRequest request) {

        log.debug("Get school content analytics request: {}", schoolId);

        // This would be implemented in the service
        ContentAnalyticsDto analytics = ContentAnalyticsDto.builder()
                .schoolId(schoolId)
                .totalPosts(125L)
                .totalGalleries(15L)
                .totalMessages(456L)
                .totalViews(25000L)
                .totalLikes(1890L)
                .totalComments(567L)
                .totalShares(234L)
                .engagementRate(12.5)
                .averagePostViews(200.0)
                .topPerformingPostId(123L)
                .mostEngagedGalleryId(45L)
                .messageResponseRate(95.2)
                .averageResponseTimeHours(2.3)
                .build();

        ApiResponse<ContentAnalyticsDto> response = ApiResponse.success(analytics, "Analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ TRENDING CONTENT ================================

    @GetMapping("/trending/posts")
    @Operation(summary = "Get trending posts", description = "Get currently trending posts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trending posts retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> getTrendingPosts(
            @Parameter(description = "Time period (24h, 7d, 30d)") @RequestParam(defaultValue = "24h") String period,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {

        log.debug("Get trending posts request - period: {}, limit: {}", period, limit);

        // This would be implemented in the service
        List<PostSummaryDto> trendingPosts = List.of(); // Placeholder

        ApiResponse<List<PostSummaryDto>> response = ApiResponse.success(trendingPosts, "Trending posts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending/hashtags")
    @Operation(summary = "Get trending hashtags", description = "Get currently trending hashtags")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trending hashtags retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<HashtagTrendDto>>> getTrendingHashtags(
            @Parameter(description = "Time period (24h, 7d, 30d)") @RequestParam(defaultValue = "24h") String period,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "20") Integer limit,
            HttpServletRequest request) {

        log.debug("Get trending hashtags request - period: {}, limit: {}", period, limit);

        // This would be implemented in the service
        List<HashtagTrendDto> trendingHashtags = List.of(
                HashtagTrendDto.builder()
                        .hashtag("#education")
                        .usageCount(156L)
                        .trendScore(8.5)
                        .growth(25.3)
                        .build(),
                HashtagTrendDto.builder()
                        .hashtag("#admissions2024")
                        .usageCount(89L)
                        .trendScore(7.2)
                        .growth(45.1)
                        .build()
        );

        ApiResponse<List<HashtagTrendDto>> response = ApiResponse.success(trendingHashtags, "Trending hashtags retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CONTENT SCHEDULING ================================

    @GetMapping("/posts/scheduled")
    @Operation(summary = "Get scheduled posts", description = "Get all scheduled posts for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Scheduled posts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> getScheduledPosts(
            @Parameter(description = "School ID") @RequestParam(required = false) Long schoolId,
            HttpServletRequest request) {

        log.debug("Get scheduled posts request for school: {}", schoolId);

        // This would be implemented in the service
        List<PostSummaryDto> scheduledPosts = List.of(); // Placeholder

        ApiResponse<List<PostSummaryDto>> response = ApiResponse.success(scheduledPosts, "Scheduled posts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/schedule")
    @Operation(summary = "Schedule post publication", description = "Schedule a post for future publication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post scheduled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid schedule time")
    })
    public ResponseEntity<ApiResponse<PostDto>> schedulePost(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @Valid @RequestBody PostScheduleRequestDto scheduleRequest,
            HttpServletRequest request) {

        log.info("Schedule post request: {} for {}", postId, scheduleRequest.getScheduledAt());

        // This would be implemented in the service - update the post with scheduled time
        PostUpdateDto updateDto = new PostUpdateDto();
        updateDto.setScheduledAt(scheduleRequest.getScheduledAt());
        updateDto.setStatus(com.genixo.education.search.enumaration.PostStatus.SCHEDULED);

        PostDto postDto = contentService.updatePost(postId, updateDto, request);

        ApiResponse<PostDto> response = ApiResponse.success(postDto, "Post scheduled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ADDITIONAL HELPER DTOs ================================

    public static class PostScheduleRequestDto {
        private LocalDateTime scheduledAt;

        public LocalDateTime getScheduledAt() { return scheduledAt; }
        public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    }

 */
}