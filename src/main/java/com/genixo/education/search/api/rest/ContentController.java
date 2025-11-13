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
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/posts/school/{id}")
    @Operation(summary = "Get post by ID", description = "Get post details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> getPostBySchoolId(
            @Parameter(description = "Post ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get post request by school id: {}", id);

        List<PostSummaryDto> postDto = contentService.getPostBySchoolId(id);

        ApiResponse<List<PostSummaryDto>> response = ApiResponse.success(postDto, "Post retrieved successfully");
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


        GalleryDto galleryDto = contentService.createGallery(createDto, request);


        ApiResponse<GalleryDto> response = ApiResponse.success(galleryDto, "Gallery created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PutMapping("/galleries/{id}")
    @Operation(summary = "Update gallery", description = "Update gallery status and details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gallery updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gallery not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<ApiResponse<GalleryDto>> createGallery(
            @Parameter(description = "Message ID") @PathVariable Long id,
            @Valid @RequestBody GalleryUpdateDto updateDto,
            HttpServletRequest request) {


        GalleryDto galleryDto = contentService.updateGallery(id, updateDto, request);

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

    @GetMapping("/galleries/school/{id}")
    @Operation(summary = "Get gallery by ID", description = "Get gallery details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gallery retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gallery not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<GalleryDto>>> getGalleryBySchoolId(
            @Parameter(description = "School ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get gallery request: {}", id);

        List<GalleryDto> galleryDto = contentService.getGalleryBySchoolId(id);

        ApiResponse<List<GalleryDto>> response = ApiResponse.success(galleryDto, "Gallery retrieved successfully");
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


        GalleryItemDto itemDto = new GalleryItemDto();
        //Ceyhun GalleryItemDto itemDto = contentService.createGalleryItem(createDto, request);

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

    @GetMapping("/messages/{id}/read")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<String>> makeReadMessage(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get message request: {}", id);

        contentService.makeReadMessage(id);

        ApiResponse<String> response = ApiResponse.success("SUCCESS", "Message retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/school/{id}/ceyhun")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessageBySchoolId(
            @Parameter(description = "Message ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get message request: {}", id);

        List<MessageDto> messageDto = contentService.getMessageBySchoolId(id, request);

        ApiResponse<List<MessageDto>> response = ApiResponse.success(messageDto, "Message retrieved successfully");
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

        MessageDto messageDto = contentService.updateMessage(id, updateDto, request);
        ApiResponse<MessageDto> response = ApiResponse.success(messageDto, "Message updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/messages/user/{userId}")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<MessageGroupDto>>> getMessageForUser(
            @Parameter(description = "userId ID") @PathVariable Long userId,
            HttpServletRequest request) {

        List<MessageGroupDto> messages = contentService.getMessageForUser(userId);

        ApiResponse<List<MessageGroupDto>> response = ApiResponse.success(messages, "Message retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }





    @GetMapping("/messages/school/{schoolId}")
    @Operation(summary = "Get message by ID", description = "Get message details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<MessageGroupDto>>> getMessageForSchool(
            @Parameter(description = "schoolId ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        List<MessageGroupDto> messages = contentService.getMessageForSchool(schoolId);

        ApiResponse<List<MessageGroupDto>> response = ApiResponse.success(messages, "Message retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

}