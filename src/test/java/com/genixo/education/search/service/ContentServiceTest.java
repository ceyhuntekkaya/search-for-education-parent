package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.content.*;
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

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContentService Tests")
class ContentServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private GalleryRepository galleryRepository;
    @Mock
    private GalleryItemRepository galleryItemRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private SchoolRepository schoolRepository;
    @Mock
    private CampusRepository campusRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ContentConverterService converterService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserAccessValidator userAccessValidator;
    @Mock
    private SlugGeneratorService slugGeneratorService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ContentService contentService;

    private User authenticatedUser;
    private School validSchool;
    private Campus validCampus;
    private Brand validBrand;
    private PostCreateDto validPostCreateDto;
    private Post savedPost;
    private PostDto expectedPostDto;

    @BeforeEach
    void setUp() {
        // User setup
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setFirstName("Test");
        authenticatedUser.setLastName("User");

        // Institution entities
        validSchool = new School();
        validSchool.setId(1L);
        validSchool.setName("Test School");
        validSchool.setIsActive(true);

        validCampus = new Campus();
        validCampus.setId(1L);
        validCampus.setName("Test Campus");
        validCampus.setIsActive(true);
        validCampus.setIsSubscribed(true);

        validBrand = new Brand();
        validBrand.setId(1L);
        validBrand.setName("Test Brand");
        validBrand.setIsActive(true);

        validSchool.setCampus(validCampus);
        validCampus.setBrand(validBrand);

        // Post create DTO
        validPostCreateDto = PostCreateDto.builder()
                .schoolId(1L)
                .title("Test Post Title")
                .content("This is test post content with detailed information.")
                .postType(PostType.TEXT)
                .status(PostStatus.DRAFT)
                .scheduledAt(null)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .featuredImageUrl("https://example.com/image.jpg")
                .videoUrl(null)
                .videoThumbnailUrl(null)
                .videoDurationSeconds(null)
                .mediaAttachments("{\"images\": []}")
                .allowComments(true)
                .allowLikes(true)
                .isFeatured(false)
                .isPinned(false)
                .pinExpiresAt(null)
                .metaTitle("Test Post Meta Title")
                .metaDescription("Test post meta description")
                .tags("education,school,test")
                .hashtags("#education #school #test")
                .externalUrl(null)
                .callToAction("Learn More")
                .ctaUrl("https://example.com/learn-more")
                .locationName("Test Location")
                .latitude(41.0082)
                .longitude(28.9784)
                .build();

        // Saved post entity
        savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setSchool(validSchool);
        savedPost.setAuthorUser(authenticatedUser);
        savedPost.setTitle("Test Post Title");
        savedPost.setSlug("test-post-title");
        savedPost.setContent("This is test post content with detailed information.");
        savedPost.setPostType(PostType.TEXT);
        savedPost.setStatus(PostStatus.DRAFT);
        savedPost.setCreatedBy(1L);
        savedPost.setCreatedAt(LocalDateTime.now());

        // Expected DTO response
        expectedPostDto = PostDto.builder()
                .id(1L)
                .title("Test Post Title")
                .slug("test-post-title")
                .content("This is test post content with detailed information.")
                .postType(PostType.TEXT)
                .status(PostStatus.DRAFT)
                .build();
    }

    // ================================ POST OPERATIONS TESTS ================================

    @Nested
    @DisplayName("createPost() Tests")
    class CreatePostTests {

        @Test
        @DisplayName("Should create post successfully with valid data")
        void shouldCreatePostSuccessfullyWithValidData() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Test Post Title", "post")).thenReturn("test-post-title");
            when(postRepository.existsBySlug("test-post-title")).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            when(converterService.mapPostToDto(savedPost)).thenReturn(expectedPostDto);

            // When
            PostDto result = contentService.createPost(validPostCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Test Post Title", result.getTitle());
            assertEquals("test-post-title", result.getSlug());
            assertEquals(PostType.TEXT, result.getPostType());
            assertEquals(PostStatus.DRAFT, result.getStatus());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(slugGeneratorService).generateUniqueSlug("Test Post Title", "post");
            verify(postRepository).existsBySlug("test-post-title");
            verify(postRepository).save(argThat(post ->
                    post.getTitle().equals("Test Post Title") &&
                            post.getContent().equals("This is test post content with detailed information.") &&
                            post.getPostType() == PostType.TEXT &&
                            post.getStatus() == PostStatus.DRAFT &&
                            post.getSchool().getId().equals(1L) &&
                            post.getAuthorUser().getId().equals(1L) &&
                            post.getSlug().equals("test-post-title") &&
                            post.getAllowComments().equals(true) &&
                            post.getAllowLikes().equals(true) &&
                            post.getIsFeatured().equals(false) &&
                            post.getIsPinned().equals(false) &&
                            post.getMetaTitle().equals("Test Post Meta Title") &&
                            post.getTags().equals("education,school,test") &&
                            post.getHashtags().equals("#education #school #test") &&
                            post.getCallToAction().equals("Learn More") &&
                            post.getLocationName().equals("Test Location") &&
                            post.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapPostToDto(savedPost);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            Long nonExistentSchoolId = 999L;
            PostCreateDto invalidSchoolDto = PostCreateDto.builder()
                    .schoolId(nonExistentSchoolId)
                    .title("Test Post")
                    .content("Test content")
                    .postType(PostType.TEXT)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(nonExistentSchoolId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.createPost(invalidSchoolDto, request));

            assertEquals("School not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(nonExistentSchoolId);
            verifyNoInteractions(userAccessValidator, slugGeneratorService, postRepository, converterService);
        }

        @Test
        @DisplayName("Should validate user access to school")
        void shouldValidateUserAccessToSchool() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doThrow(new BusinessException("User does not have access to this school"))
                    .when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.createPost(validPostCreateDto, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verifyNoInteractions(slugGeneratorService, postRepository);
        }

        @Test
        @DisplayName("Should generate unique slug when slug already exists")
        void shouldGenerateUniqueSlugWhenSlugExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Test Post Title", "post")).thenReturn("test-post-title");
            when(postRepository.existsBySlug("test-post-title")).thenReturn(true);
            when(slugGeneratorService.generateUniqueSlug(startsWith("Test Post Title-"), eq("post")))
                    .thenReturn("test-post-title-123456789");
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            when(converterService.mapPostToDto(any(Post.class))).thenReturn(expectedPostDto);

            // When
            PostDto result = contentService.createPost(validPostCreateDto, request);

            // Then
            assertNotNull(result);

            verify(slugGeneratorService).generateUniqueSlug("Test Post Title", "post");
            verify(postRepository).existsBySlug("test-post-title");
            verify(slugGeneratorService).generateUniqueSlug(startsWith("Test Post Title-"), eq("post"));
            verify(postRepository).save(argThat(post ->
                    post.getSlug().equals("test-post-title-123456789")
            ));
        }

        @Test
        @DisplayName("Should set published date when status is PUBLISHED")
        void shouldSetPublishedDateWhenStatusIsPublished() {
            // Given
            PostCreateDto publishedPostDto = PostCreateDto.builder()
                    .schoolId(1L)
                    .title("Published Post")
                    .content("Published content")
                    .postType(PostType.TEXT)
                    .status(PostStatus.PUBLISHED)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Published Post", "post")).thenReturn("published-post");
            when(postRepository.existsBySlug("published-post")).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            when(converterService.mapPostToDto(any(Post.class))).thenReturn(expectedPostDto);

            // When
            contentService.createPost(publishedPostDto, request);

            // Then
            verify(postRepository).save(argThat(post ->
                    post.getStatus() == PostStatus.PUBLISHED &&
                            post.getPublishedAt() != null
            ));
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            PostCreateDto minimalDto = PostCreateDto.builder()
                    .schoolId(1L)
                    .title("Minimal Post")
                    .content("Minimal content")
                    .postType(PostType.TEXT)
                    // Optional fields not set
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Minimal Post", "post")).thenReturn("minimal-post");
            when(postRepository.existsBySlug("minimal-post")).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            when(converterService.mapPostToDto(any(Post.class))).thenReturn(expectedPostDto);

            // When
            contentService.createPost(minimalDto, request);

            // Then
            verify(postRepository).save(argThat(post ->
                    post.getStatus() == PostStatus.DRAFT && // Default status
                            post.getAllowComments().equals(true) && // Default allowComments
                            post.getAllowLikes().equals(true) && // Default allowLikes
                            post.getIsFeatured().equals(false) && // Default isFeatured
                            post.getIsPinned().equals(false) // Default isPinned
            ));
        }

        @Test
        @DisplayName("Should handle all media types and video fields")
        void shouldHandleAllMediaTypesAndVideoFields() {
            // Given
            PostCreateDto videoPostDto = PostCreateDto.builder()
                    .schoolId(1L)
                    .title("Video Post")
                    .content("Video content")
                    .postType(PostType.VIDEO)
                    .videoUrl("https://example.com/video.mp4")
                    .videoThumbnailUrl("https://example.com/thumb.jpg")
                    .videoDurationSeconds(300)
                    .mediaAttachments("{\"videos\": [{\"url\": \"video.mp4\"}]}")
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Video Post", "post")).thenReturn("video-post");
            when(postRepository.existsBySlug("video-post")).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            when(converterService.mapPostToDto(any(Post.class))).thenReturn(expectedPostDto);

            // When
            contentService.createPost(videoPostDto, request);

            // Then
            verify(postRepository).save(argThat(post ->
                    post.getPostType() == PostType.VIDEO &&
                            post.getVideoUrl().equals("https://example.com/video.mp4") &&
                            post.getVideoThumbnailUrl().equals("https://example.com/thumb.jpg") &&
                            post.getVideoDurationSeconds().equals(300) &&
                            post.getMediaAttachments().equals("{\"videos\": [{\"url\": \"video.mp4\"}]}")
            ));
        }
    }

    @Nested
    @DisplayName("createPostComment() Tests")
    class CreatePostCommentTests {

        private PostCommentCreateDto validCommentCreateDto;
        private Post publishedPost;
        private PostComment savedComment;
        private PostCommentDto expectedCommentDto;

        @BeforeEach
        void setUp() {
            validCommentCreateDto = PostCommentCreateDto.builder()
                    .postId(1L)
                    .parentCommentId(null)
                    .content("This is a great post! Thanks for sharing.")
                    .build();

            publishedPost = new Post();
            publishedPost.setId(1L);
            publishedPost.setTitle("Published Post");
            publishedPost.setStatus(PostStatus.PUBLISHED);
            publishedPost.setAllowComments(true);

            savedComment = new PostComment();
            savedComment.setId(1L);
            savedComment.setPost(publishedPost);
            savedComment.setUser(authenticatedUser);
            savedComment.setContent("This is a great post! Thanks for sharing.");
            savedComment.setStatus(CommentStatus.PUBLISHED);
            savedComment.setCreatedBy(1L);

            expectedCommentDto = PostCommentDto.builder()
                    .id(1L)
                    .postId(1L)
                    .content("This is a great post! Thanks for sharing.")
                    .status(CommentStatus.PUBLISHED)
                    .build();
        }

        @Test
        @DisplayName("Should create comment successfully for published post")
        void shouldCreateCommentSuccessfullyForPublishedPost() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.save(any(PostComment.class))).thenReturn(savedComment);
            //when(postRepository.incrementCommentCount(1L)).thenReturn(1); ceyhun

            doNothing().when(postRepository).incrementCommentCount(1L);
            when(converterService.mapPostCommentToDto(savedComment)).thenReturn(expectedCommentDto);

            // When
            PostCommentDto result = contentService.createPostComment(validCommentCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("This is a great post! Thanks for sharing.", result.getContent());
            assertEquals(CommentStatus.PUBLISHED, result.getStatus());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED);
            verify(postCommentRepository).save(argThat(comment ->
                    comment.getPost().getId().equals(1L) &&
                            comment.getUser().getId().equals(1L) &&
                            comment.getParentComment() == null &&
                            comment.getContent().equals("This is a great post! Thanks for sharing.") &&
                            comment.getStatus() == CommentStatus.PUBLISHED &&
                            comment.getCreatedBy().equals(1L)
            ));
            verify(postRepository).incrementCommentCount(1L);
            verify(converterService).mapPostCommentToDto(savedComment);
        }

        @Test
        @DisplayName("Should create reply comment with parent comment")
        void shouldCreateReplyCommentWithParentComment() {
            // Given
            Long parentCommentId = 2L;
            PostComment parentComment = new PostComment();
            parentComment.setId(parentCommentId);
            parentComment.setPost(publishedPost);

            PostCommentCreateDto replyDto = PostCommentCreateDto.builder()
                    .postId(1L)
                    .parentCommentId(parentCommentId)
                    .content("Great reply to the comment!")
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.findByIdAndIsActiveTrue(parentCommentId))
                    .thenReturn(Optional.of(parentComment));
            when(postCommentRepository.save(any(PostComment.class))).thenReturn(savedComment);
            //when(postRepository.incrementCommentCount(1L)).thenReturn(1); ceyhun
            //when(postCommentRepository.incrementReplyCount(parentCommentId)).thenReturn(1);


            doNothing().when(postRepository).incrementCommentCount(1L);
            doNothing().when(postCommentRepository).incrementReplyCount(parentCommentId);



            when(converterService.mapPostCommentToDto(savedComment)).thenReturn(expectedCommentDto);

            // When
            PostCommentDto result = contentService.createPostComment(replyDto, request);

            // Then
            assertNotNull(result);

            verify(postCommentRepository).findByIdAndIsActiveTrue(parentCommentId);
            verify(postCommentRepository).save(argThat(comment ->
                    comment.getParentComment().getId().equals(parentCommentId)
            ));
            verify(postRepository).incrementCommentCount(1L);
            verify(postCommentRepository).incrementReplyCount(parentCommentId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when post not found or not published")
        void shouldThrowResourceNotFoundExceptionWhenPostNotFoundOrNotPublished() {
            // Given
            Long nonExistentPostId = 999L;
            PostCommentCreateDto invalidPostDto = PostCommentCreateDto.builder()
                    .postId(nonExistentPostId)
                    .content("Comment on non-existent post")
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.createPostComment(invalidPostDto, request));

            assertEquals("Post not found or not published", exception.getMessage());

            verify(postRepository).findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED);
            verifyNoInteractions(postCommentRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when comments are not allowed")
        void shouldThrowBusinessExceptionWhenCommentsAreNotAllowed() {
            // Given
            Post postWithCommentsDisabled = new Post();
            postWithCommentsDisabled.setId(1L);
            postWithCommentsDisabled.setStatus(PostStatus.PUBLISHED);
            postWithCommentsDisabled.setAllowComments(false);

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(postWithCommentsDisabled));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.createPostComment(validCommentCreateDto, request));

            assertEquals("Comments are not allowed on this post", exception.getMessage());

            verify(postRepository).findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED);
            verifyNoInteractions(postCommentRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when parent comment not found")
        void shouldThrowResourceNotFoundExceptionWhenParentCommentNotFound() {
            // Given
            Long nonExistentParentId = 999L;
            PostCommentCreateDto invalidParentDto = PostCommentCreateDto.builder()
                    .postId(1L)
                    .parentCommentId(nonExistentParentId)
                    .content("Reply to non-existent comment")
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.findByIdAndIsActiveTrue(nonExistentParentId))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.createPostComment(invalidParentDto, request));

            assertEquals("Parent comment not found", exception.getMessage());

            verify(postRepository).findByIdAndStatusAndIsActiveTrue(1L, PostStatus.PUBLISHED);
            verify(postCommentRepository).findByIdAndIsActiveTrue(nonExistentParentId);
            verify(postCommentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getPostComments() Tests")
    class GetPostCommentsTests {

        private Post publishedPost;
        private List<PostComment> mockComments;
        private org.springframework.data.domain.Page<PostComment> mockCommentsPage;

        @BeforeEach
        void setUp() {
            publishedPost = new Post();
            publishedPost.setId(1L);
            publishedPost.setStatus(PostStatus.PUBLISHED);

            PostComment comment1 = new PostComment();
            comment1.setId(1L);
            comment1.setContent("First comment");

            PostComment comment2 = new PostComment();
            comment2.setId(2L);
            comment2.setContent("Second comment");

            mockComments = List.of(comment1, comment2);

            mockCommentsPage = new org.springframework.data.domain.PageImpl<>(
                    mockComments,
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    2L
            );
        }

        @Test
        @DisplayName("Should return post comments with default pagination")
        void shouldReturnPostCommentsWithDefaultPagination() {
            // Given
            Long postId = 1L;
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId), eq(CommentStatus.PUBLISHED), any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(mockCommentsPage);
            when(converterService.mapPostCommentToDto(any(PostComment.class)))
                    .thenReturn(PostCommentDto.builder().content("Mock comment").build());

            // When
            org.springframework.data.domain.Page<PostCommentDto> result =
                    contentService.getPostComments(postId, null, null);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());

            verify(postRepository).findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED);
            verify(postCommentRepository).findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId), eq(CommentStatus.PUBLISHED),
                    argThat(pageable ->
                            pageable.getPageNumber() == 0 &&
                                    pageable.getPageSize() == 20 &&
                                    pageable.getSort().getOrderFor("createdAt").getDirection() ==
                                            org.springframework.data.domain.Sort.Direction.DESC
                    )
            );
            verify(converterService, times(2)).mapPostCommentToDto(any(PostComment.class));
        }

        @Test
        @DisplayName("Should return post comments with custom pagination")
        void shouldReturnPostCommentsWithCustomPagination() {
            // Given
            Long postId = 1L;
            Integer page = 1;
            Integer size = 10;

            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId), eq(CommentStatus.PUBLISHED), any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(mockCommentsPage);
            when(converterService.mapPostCommentToDto(any(PostComment.class)))
                    .thenReturn(PostCommentDto.builder().content("Mock comment").build());

            // When
            contentService.getPostComments(postId, page, size);

            // Then
            verify(postCommentRepository).findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId), eq(CommentStatus.PUBLISHED),
                    argThat(pageable ->
                            pageable.getPageNumber() == 1 &&
                                    pageable.getPageSize() == 10
                    )
            );
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when post not found or not published")
        void shouldThrowResourceNotFoundExceptionWhenPostNotFoundOrNotPublished() {
            // Given
            Long nonExistentPostId = 999L;
            when(postRepository.findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.getPostComments(nonExistentPostId, null, null));

            assertEquals("Post not found or not published", exception.getMessage());

            verify(postRepository).findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED);
            verifyNoInteractions(postCommentRepository, converterService);
        }

        @Test
        @DisplayName("Should only return published comments sorted by creation date descending")
        void shouldOnlyReturnPublishedCommentsSortedByCreationDateDescending() {
            // Given
            Long postId = 1L;
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postCommentRepository.findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId), eq(CommentStatus.PUBLISHED), any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(mockCommentsPage);
            when(converterService.mapPostCommentToDto(any(PostComment.class)))
                    .thenReturn(PostCommentDto.builder().content("Mock comment").build());

            // When
            contentService.getPostComments(postId, null, null);

            // Then
            verify(postCommentRepository).findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
                    eq(postId),
                    eq(CommentStatus.PUBLISHED), // Only published comments
                    argThat(pageable -> {
                        org.springframework.data.domain.Sort.Order order =
                                pageable.getSort().getOrderFor("createdAt");
                        return order != null &&
                                order.getDirection() == org.springframework.data.domain.Sort.Direction.DESC;
                    })
            );
        }
    }

    @Nested
    @DisplayName("createGalleryItem() Tests")
    class CreateGalleryItemTests {

        private GalleryItemCreateDto validItemCreateDto;
        private Gallery existingGallery;
        private GalleryItem savedItem;
        private GalleryItemDto expectedItemDto;

        @BeforeEach
        void setUp() {
            validItemCreateDto = GalleryItemCreateDto.builder()
                    .galleryId(1L)
                    .title("Beautiful Sunset")
                    .description("A stunning sunset photo from our school garden")
                    .altText("Sunset over school garden")
                    .itemType(MediaType.IMAGE)
                    .fileUrl("https://example.com/sunset.jpg")
                    .thumbnailUrl("https://example.com/sunset-thumb.jpg")
                    .fileName("sunset.jpg")
                    .originalFileName("IMG_20240315_sunset.jpg")
                    .fileSizeBytes(2048000L)
                    .mimeType("image/jpeg")
                    .width(1920)
                    .height(1080)
                    .durationSeconds(null)
                    .videoFormat(null)
                    .cameraMake("Canon")
                    .cameraModel("EOS R5")
                    .takenAt(LocalDateTime.now().minusDays(1))
                    .locationName("School Garden")
                    .latitude(41.0082)
                    .longitude(28.9784)
                    .sortOrder(1)
                    .isFeatured(true)
                    .isCover(false)
                    .tags("sunset,garden,nature")
                    .build();

            existingGallery = new Gallery();
            existingGallery.setId(1L);
            existingGallery.setTitle("School Photos");
            existingGallery.setSchool(validSchool);

            savedItem = new GalleryItem();
            savedItem.setId(1L);
            savedItem.setGallery(existingGallery);
            savedItem.setTitle("Beautiful Sunset");
            savedItem.setCreatedBy(1L);

            expectedItemDto = GalleryItemDto.builder()
                    .id(1L)
                    .galleryId(1L)
                    .title("Beautiful Sunset")
                    .itemType(MediaType.IMAGE)
                    .build();
        }

        @Test
        @DisplayName("Should create gallery item successfully with all fields")
        void shouldCreateGalleryItemSuccessfullyWithAllFields() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doNothing().when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            when(galleryItemRepository.save(any(GalleryItem.class))).thenReturn(savedItem);
            //when(galleryRepository.incrementItemCount(1L)).thenReturn(1); ceyhun
            //when(galleryRepository.addToTotalSize(1L, 2048000L)).thenReturn(1);



            doNothing().when(galleryRepository).incrementItemCount(1L);
            doNothing().when(galleryRepository).addToTotalSize(1L, 2048000L);



            when(converterService.mapGalleryItemToDto(savedItem)).thenReturn(expectedItemDto);

            // When
            GalleryItemDto result = contentService.createGalleryItem(validItemCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Beautiful Sunset", result.getTitle());
            assertEquals(MediaType.IMAGE, result.getItemType());

            verify(jwtService).getUser(request);
            verify(galleryRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            verify(galleryItemRepository).save(argThat(item ->
                    item.getGallery().getId().equals(1L) &&
                            item.getUploadedByUser().getId().equals(1L) &&
                            item.getTitle().equals("Beautiful Sunset") &&
                            item.getDescription().equals("A stunning sunset photo from our school garden") &&
                            item.getAltText().equals("Sunset over school garden") &&
                            item.getItemType() == MediaType.IMAGE &&
                            item.getFileUrl().equals("https://example.com/sunset.jpg") &&
                            item.getThumbnailUrl().equals("https://example.com/sunset-thumb.jpg") &&
                            item.getFileName().equals("sunset.jpg") &&
                            item.getOriginalFileName().equals("IMG_20240315_sunset.jpg") &&
                            item.getFileSizeBytes().equals(2048000L) &&
                            item.getMimeType().equals("image/jpeg") &&
                            item.getWidth().equals(1920) &&
                            item.getHeight().equals(1080) &&
                            item.getCameraMake().equals("Canon") &&
                            item.getCameraModel().equals("EOS R5") &&
                            item.getLocationName().equals("School Garden") &&
                            item.getLatitude().equals(41.0082) &&
                            item.getLongitude().equals(28.9784) &&
                            item.getSortOrder().equals(1) &&
                            item.getIsFeatured().equals(true) &&
                            item.getIsCover().equals(false) &&
                            item.getTags().equals("sunset,garden,nature") &&
                            item.getProcessingStatus() == ProcessingStatus.COMPLETED &&
                            item.getCreatedBy().equals(1L)
            ));
            verify(galleryRepository).incrementItemCount(1L);
            verify(galleryRepository).addToTotalSize(1L, 2048000L);
            verify(converterService).mapGalleryItemToDto(savedItem);
        }

        @Test
        @DisplayName("Should calculate aspect ratio for images with dimensions")
        void shouldCalculateAspectRatioForImagesWithDimensions() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doNothing().when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            when(galleryItemRepository.save(any(GalleryItem.class))).thenReturn(savedItem);
           // when(galleryRepository.incrementItemCount(1L)).thenReturn(1); ceyhun
           // when(galleryRepository.addToTotalSize(anyLong(), anyLong())).thenReturn(1);
            when(converterService.mapGalleryItemToDto(any(GalleryItem.class))).thenReturn(expectedItemDto);


            doNothing().when(galleryRepository).incrementItemCount(1L);
            doNothing().when(galleryRepository).addToTotalSize(anyLong(), anyLong());

            // When
            contentService.createGalleryItem(validItemCreateDto, request);

            // Then
            verify(galleryItemRepository).save(argThat(item -> {
                // 1920/1080 = 1.78 (rounded to 2 decimal places)
                String expectedRatio = String.format("%.2f", 1920.0 / 1080.0);
                return item.getAspectRatio().equals(expectedRatio);
            }));
        }

        @Test
        @DisplayName("Should handle video files with duration")
        void shouldHandleVideoFilesWithDuration() {
            // Given
            GalleryItemCreateDto videoItemDto = GalleryItemCreateDto.builder()
                    .galleryId(1L)
                    .title("School Event Video")
                    .itemType(MediaType.VIDEO)
                    .fileUrl("https://example.com/event.mp4")
                    .width(1280)
                    .height(720)
                    .durationSeconds(300)
                    .videoFormat("mp4")
                    .fileSizeBytes(50000000L)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doNothing().when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            when(galleryItemRepository.save(any(GalleryItem.class))).thenReturn(savedItem);
            //when(galleryRepository.incrementItemCount(1L)).thenReturn(1); ceyhun
            //when(galleryRepository.addToTotalSize(1L, 50000000L)).thenReturn(1);


            doNothing().when(galleryRepository).incrementItemCount(1L);
            doNothing().when(galleryRepository).addToTotalSize(1L, 50000000L);


            when(converterService.mapGalleryItemToDto(any(GalleryItem.class))).thenReturn(expectedItemDto);

            // When
            contentService.createGalleryItem(videoItemDto, request);

            // Then
            verify(galleryItemRepository).save(argThat(item ->
                    item.getItemType() == MediaType.VIDEO &&
                            item.getDurationSeconds().equals(300) &&
                            item.getVideoFormat().equals("mp4")
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when gallery not found")
        void shouldThrowResourceNotFoundExceptionWhenGalleryNotFound() {
            // Given
            Long nonExistentGalleryId = 999L;
            GalleryItemCreateDto invalidGalleryDto = GalleryItemCreateDto.builder()
                    .galleryId(nonExistentGalleryId)
                    .title("Item for non-existent gallery")
                    .itemType(MediaType.IMAGE)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(nonExistentGalleryId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.createGalleryItem(invalidGalleryDto, request));

            assertEquals("Gallery not found", exception.getMessage());

            verify(galleryRepository).findByIdAndIsActiveTrue(nonExistentGalleryId);
            verifyNoInteractions(userAccessValidator, galleryItemRepository);
        }

        @Test
        @DisplayName("Should validate user access to gallery")
        void shouldValidateUserAccessToGallery() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doThrow(new BusinessException("User does not have access to this gallery"))
                    .when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.createGalleryItem(validItemCreateDto, request));

            assertEquals("User does not have access to this gallery", exception.getMessage());

            verify(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            verifyNoInteractions(galleryItemRepository);
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            GalleryItemCreateDto minimalDto = GalleryItemCreateDto.builder()
                    .galleryId(1L)
                    .title("Minimal Item")
                    .itemType(MediaType.IMAGE)
                    .fileUrl("https://example.com/minimal.jpg")
                    // Optional fields not set
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doNothing().when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            when(galleryItemRepository.save(any(GalleryItem.class))).thenReturn(savedItem);
           // when(galleryRepository.incrementItemCount(1L)).thenReturn(1); ceyhun

            doNothing().when(galleryRepository).incrementItemCount(1L);


            when(converterService.mapGalleryItemToDto(any(GalleryItem.class))).thenReturn(expectedItemDto);

            // When
            contentService.createGalleryItem(minimalDto, request);

            // Then
            verify(galleryItemRepository).save(argThat(item ->
                    item.getSortOrder().equals(0) && // Default sortOrder
                            item.getIsFeatured().equals(false) && // Default isFeatured
                            item.getIsCover().equals(false) && // Default isCover
                            item.getProcessingStatus() == ProcessingStatus.COMPLETED // Default processing status
            ));
        }

        @Test
        @DisplayName("Should not update gallery stats when file size is null")
        void shouldNotUpdateGalleryStatsWhenFileSizeIsNull() {
            // Given
            GalleryItemCreateDto noSizeDto = GalleryItemCreateDto.builder()
                    .galleryId(1L)
                    .title("No Size Item")
                    .itemType(MediaType.IMAGE)
                    .fileUrl("https://example.com/nosize.jpg")
                    .fileSizeBytes(null)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(galleryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingGallery));
            doNothing().when(userAccessValidator).validateUserCanAccessGallery(authenticatedUser, existingGallery);
            when(galleryItemRepository.save(any(GalleryItem.class))).thenReturn(savedItem);
            //when(galleryRepository.incrementItemCount(1L)).thenReturn(1); ceyhun

            doNothing().when(galleryRepository).incrementItemCount(1L);


            when(converterService.mapGalleryItemToDto(any(GalleryItem.class))).thenReturn(expectedItemDto);

            // When
            contentService.createGalleryItem(noSizeDto, request);

            // Then
            verify(galleryRepository).incrementItemCount(1L);
            verify(galleryRepository, never()).addToTotalSize(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("updatePost() Tests")
    class UpdatePostTests {

        private PostUpdateDto validUpdateDto;
        private Post existingPost;

        @BeforeEach
        void setUp() {
            validUpdateDto = PostUpdateDto.builder()
                    .title("Updated Post Title")
                    .content("Updated post content")
                    .status(PostStatus.PUBLISHED)
                    .scheduledAt(LocalDateTime.now().plusHours(2))
                    .expiresAt(LocalDateTime.now().plusDays(30))
                    .featuredImageUrl("https://example.com/updated-image.jpg")
                    .videoUrl("https://example.com/updated-video.mp4")
                    .videoThumbnailUrl("https://example.com/updated-thumb.jpg")
                    .mediaAttachments("{\"images\": [\"updated.jpg\"]}")
                    .allowComments(false)
                    .allowLikes(true)
                    .isFeatured(true)
                    .isPinned(true)
                    .pinExpiresAt(LocalDateTime.now().plusDays(7))
                    .metaTitle("Updated Meta Title")
                    .metaDescription("Updated meta description")
                    .tags("updated,tags")
                    .hashtags("#updated #tags")
                    .externalUrl("https://example.com/updated")
                    .callToAction("Updated CTA")
                    .ctaUrl("https://example.com/updated-cta")
                    .build();

            existingPost = new Post();
            existingPost.setId(1L);
            existingPost.setSchool(validSchool);
            existingPost.setAuthorUser(authenticatedUser);
            existingPost.setTitle("Original Title");
            existingPost.setContent("Original content");
            existingPost.setStatus(PostStatus.DRAFT);
            existingPost.setPublishedAt(null);
        }

        @Test
        @DisplayName("Should update post successfully with all fields")
        void shouldUpdatePostSuccessfullyWithAllFields() {
            // Given
            Long postId = 1L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndIsActiveTrue(postId)).thenReturn(Optional.of(existingPost));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(postRepository.save(any(Post.class))).thenReturn(existingPost);
            when(converterService.mapPostToDto(existingPost)).thenReturn(expectedPostDto);

            // When
            PostDto result = contentService.updatePost(postId, validUpdateDto, request);

            // Then
            assertNotNull(result);

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndIsActiveTrue(postId);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(postRepository).save(argThat(post ->
                    post.getTitle().equals("Updated Post Title") &&
                            post.getContent().equals("Updated post content") &&
                            post.getStatus() == PostStatus.PUBLISHED &&
                            post.getFeaturedImageUrl().equals("https://example.com/updated-image.jpg") &&
                            post.getVideoUrl().equals("https://example.com/updated-video.mp4") &&
                            post.getVideoThumbnailUrl().equals("https://example.com/updated-thumb.jpg") &&
                            post.getMediaAttachments().equals("{\"images\": [\"updated.jpg\"]}") &&
                            post.getAllowComments().equals(false) &&
                            post.getAllowLikes().equals(true) &&
                            post.getIsFeatured().equals(true) &&
                            post.getIsPinned().equals(true) &&
                            post.getMetaTitle().equals("Updated Meta Title") &&
                            post.getMetaDescription().equals("Updated meta description") &&
                            post.getTags().equals("updated,tags") &&
                            post.getHashtags().equals("#updated #tags") &&
                            post.getExternalUrl().equals("https://example.com/updated") &&
                            post.getCallToAction().equals("Updated CTA") &&
                            post.getCtaUrl().equals("https://example.com/updated-cta") &&
                            post.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapPostToDto(existingPost);
        }

        @Test
        @DisplayName("Should set published date when status changes to PUBLISHED")
        void shouldSetPublishedDateWhenStatusChangesToPublished() {
            // Given
            Long postId = 1L;
            existingPost.setStatus(PostStatus.DRAFT);
            existingPost.setPublishedAt(null);

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndIsActiveTrue(postId)).thenReturn(Optional.of(existingPost));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(postRepository.save(any(Post.class))).thenReturn(existingPost);
            when(converterService.mapPostToDto(existingPost)).thenReturn(expectedPostDto);

            // When
            contentService.updatePost(postId, validUpdateDto, request);

            // Then
            verify(postRepository).save(argThat(post ->
                    post.getStatus() == PostStatus.PUBLISHED &&
                            post.getPublishedAt() != null
            ));
        }

        @Test
        @DisplayName("Should not change published date when status is already PUBLISHED")
        void shouldNotChangePublishedDateWhenStatusIsAlreadyPublished() {
            // Given
            Long postId = 1L;
            LocalDateTime originalPublishedDate = LocalDateTime.now().minusDays(1);
            existingPost.setStatus(PostStatus.PUBLISHED);
            existingPost.setPublishedAt(originalPublishedDate);

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndIsActiveTrue(postId)).thenReturn(Optional.of(existingPost));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(postRepository.save(any(Post.class))).thenReturn(existingPost);
            when(converterService.mapPostToDto(existingPost)).thenReturn(expectedPostDto);

            // When
            contentService.updatePost(postId, validUpdateDto, request);

            // Then
            verify(postRepository).save(argThat(post ->
                    post.getStatus() == PostStatus.PUBLISHED &&
                            post.getPublishedAt().equals(originalPublishedDate) // Should remain unchanged
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when post not found")
        void shouldThrowResourceNotFoundExceptionWhenPostNotFound() {
            // Given
            Long nonExistentPostId = 999L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndIsActiveTrue(nonExistentPostId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.updatePost(nonExistentPostId, validUpdateDto, request));

            assertEquals("Post not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndIsActiveTrue(nonExistentPostId);
            verifyNoInteractions(userAccessValidator);
            verify(postRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate user access to post's school")
        void shouldValidateUserAccessToPostsSchool() {
            // Given
            Long postId = 1L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndIsActiveTrue(postId)).thenReturn(Optional.of(existingPost));
            doThrow(new BusinessException("User does not have access to this school"))
                    .when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.updatePost(postId, validUpdateDto, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndIsActiveTrue(postId);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(postRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getPublicPostBySlug() Tests")
    class GetPublicPostBySlugTests {

        private Post publishedPost;
        private Campus subscribedCampus;

        @BeforeEach
        void setUp() {
            subscribedCampus = new Campus();
            subscribedCampus.setId(1L);
            subscribedCampus.setIsSubscribed(true);

            School schoolWithSubscribedCampus = new School();
            schoolWithSubscribedCampus.setId(1L);
            schoolWithSubscribedCampus.setCampus(subscribedCampus);

            publishedPost = new Post();
            publishedPost.setId(1L);
            publishedPost.setTitle("Public Post");
            publishedPost.setSlug("public-post");
            publishedPost.setStatus(PostStatus.PUBLISHED);
            publishedPost.setSchool(schoolWithSubscribedCampus);
        }

        @Test
        @DisplayName("Should return published post from subscribed campus successfully")
        void shouldReturnPublishedPostFromSubscribedCampusSuccessfully() {
            // Given
            String slug = "public-post";
            when(postRepository.findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            // when(postRepository.incrementViewCount(1L)).thenReturn(1); ceyhun


            doNothing().when(postRepository).incrementViewCount(1L);


            when(converterService.mapPostToDto(publishedPost)).thenReturn(expectedPostDto);

            // When
            PostDto result = contentService.getPublicPostBySlug(slug);

            // Then
            assertNotNull(result);
            assertEquals(expectedPostDto.getTitle(), result.getTitle());

            verify(postRepository).findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED);
            verify(postRepository).incrementViewCount(1L);
            verify(converterService).mapPostToDto(publishedPost);
            verifyNoInteractions(jwtService); // Should not require authentication
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when post not found or not published")
        void shouldThrowResourceNotFoundExceptionWhenPostNotFoundOrNotPublished() {
            // Given
            String nonExistentSlug = "non-existent-post";
            when(postRepository.findBySlugAndStatusAndIsActiveTrue(nonExistentSlug, PostStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.getPublicPostBySlug(nonExistentSlug));

            assertEquals("Post not found or not published", exception.getMessage());

            verify(postRepository).findBySlugAndStatusAndIsActiveTrue(nonExistentSlug, PostStatus.PUBLISHED);
            verify(postRepository, never()).incrementViewCount(anyLong());
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school campus is not subscribed")
        void shouldThrowResourceNotFoundExceptionWhenSchoolCampusIsNotSubscribed() {
            // Given
            String slug = "unsubscribed-post";
            Campus unsubscribedCampus = new Campus();
            unsubscribedCampus.setId(1L);
            unsubscribedCampus.setIsSubscribed(false);

            School schoolWithUnsubscribedCampus = new School();
            schoolWithUnsubscribedCampus.setId(1L);
            schoolWithUnsubscribedCampus.setCampus(unsubscribedCampus);

            Post postFromUnsubscribedCampus = new Post();
            postFromUnsubscribedCampus.setId(1L);
            postFromUnsubscribedCampus.setSlug(slug);
            postFromUnsubscribedCampus.setStatus(PostStatus.PUBLISHED);
            postFromUnsubscribedCampus.setSchool(schoolWithUnsubscribedCampus);

            when(postRepository.findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(postFromUnsubscribedCampus));

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.getPublicPostBySlug(slug));

            assertEquals("Post not available", exception.getMessage());

            verify(postRepository).findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED);
            verify(postRepository, never()).incrementViewCount(anyLong());
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should increment view count when post is accessed publicly")
        void shouldIncrementViewCountWhenPostIsAccessedPublicly() {
            // Given
            String slug = "popular-post";
            publishedPost.setSlug(slug);

            when(postRepository.findBySlugAndStatusAndIsActiveTrue(slug, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            // when(postRepository.incrementViewCount(1L)).thenReturn(1); ceyhun

            doNothing().when(postRepository).incrementViewCount(1L);


            when(converterService.mapPostToDto(publishedPost)).thenReturn(expectedPostDto);

            // When
            contentService.getPublicPostBySlug(slug);

            // Then
            verify(postRepository).incrementViewCount(1L);
        }
    }

    @Nested
    @DisplayName("updateMessage() Tests")
    class UpdateMessageTests {

        private MessageUpdateDto validMessageUpdateDto;
        private Message existingMessage;
        private MessageDto expectedMessageDto;

        @BeforeEach
        void setUp() {
            validMessageUpdateDto = MessageUpdateDto.builder()
                    .status(MessageStatus.RESPONDED)
                    .priority(MessagePriority.HIGH)
                    .assignedToUserId(2L)
                    .internalNotes("Message has been reviewed and responded to")
                    .tags("enrollment,urgent")
                    .followUpRequired(true)
                    .followUpDate(LocalDateTime.now().plusDays(3))
                    .followUpNotes("Follow up on enrollment application")
                    .build();

            existingMessage = new Message();
            existingMessage.setId(1L);
            existingMessage.setSchool(validSchool);
            existingMessage.setSenderName("John Doe");
            existingMessage.setStatus(MessageStatus.NEW);
            existingMessage.setFirstResponseAt(null);
            existingMessage.setResolvedAt(null);
            existingMessage.setCreatedAt(LocalDateTime.now().minusHours(2));

            expectedMessageDto = MessageDto.builder()
                    .id(1L)
                    .senderName("John Doe")
                    .status(MessageStatus.RESPONDED)
                    .priority(MessagePriority.HIGH)
                    .build();
        }

        @Test
        @DisplayName("Should update message successfully with all fields")
        void shouldUpdateMessageSuccessfullyWithAllFields() {
            // Given
            Long messageId = 1L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
            when(converterService.mapMessageToDto(existingMessage)).thenReturn(expectedMessageDto);

            // When
            MessageDto result = contentService.updateMessage(messageId, validMessageUpdateDto, request);

            // Then
            assertNotNull(result);
            assertEquals(MessageStatus.RESPONDED, result.getStatus());
            assertEquals(MessagePriority.HIGH, result.getPriority());

            verify(jwtService).getUser(request);
            verify(messageRepository).findByIdAndIsActiveTrue(messageId);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(messageRepository).save(argThat(message ->
                    message.getStatus() == MessageStatus.RESPONDED &&
                            message.getPriority() == MessagePriority.HIGH &&
                            message.getInternalNotes().equals("Message has been reviewed and responded to") &&
                            message.getTags().equals("enrollment,urgent") &&
                            message.getFollowUpRequired().equals(true) &&
                            message.getFollowUpNotes().equals("Follow up on enrollment application") &&
                            message.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapMessageToDto(existingMessage);
        }

        @Test
        @DisplayName("Should set first response time when status changes to RESPONDED")
        void shouldSetFirstResponseTimeWhenStatusChangesToResponded() {
            // Given
            Long messageId = 1L;
            existingMessage.setFirstResponseAt(null);

            MessageUpdateDto respondedUpdateDto = MessageUpdateDto.builder()
                    .status(MessageStatus.RESPONDED)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
            when(converterService.mapMessageToDto(existingMessage)).thenReturn(expectedMessageDto);

            // When
            contentService.updateMessage(messageId, respondedUpdateDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getStatus() == MessageStatus.RESPONDED &&
                            message.getFirstResponseAt() != null &&
                            message.getResponseTimeHours() != null &&
                            message.getResponseTimeHours() > 0
            ));
        }

        @Test
        @DisplayName("Should set first response time when status changes to IN_PROGRESS")
        void shouldSetFirstResponseTimeWhenStatusChangesToInProgress() {
            // Given
            Long messageId = 1L;
            existingMessage.setFirstResponseAt(null);

            MessageUpdateDto inProgressUpdateDto = MessageUpdateDto.builder()
                    .status(MessageStatus.IN_PROGRESS)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
            when(converterService.mapMessageToDto(existingMessage)).thenReturn(expectedMessageDto);

            // When
            contentService.updateMessage(messageId, inProgressUpdateDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getStatus() == MessageStatus.IN_PROGRESS &&
                            message.getFirstResponseAt() != null
            ));
        }

        @Test
        @DisplayName("Should set resolved time and calculate resolution duration when status changes to RESOLVED")
        void shouldSetResolvedTimeAndCalculateResolutionDurationWhenStatusChangesToResolved() {
            // Given
            Long messageId = 1L;
            existingMessage.setResolvedAt(null);

            MessageUpdateDto resolvedUpdateDto = MessageUpdateDto.builder()
                    .status(MessageStatus.RESOLVED)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
            when(converterService.mapMessageToDto(existingMessage)).thenReturn(expectedMessageDto);

            // When
            contentService.updateMessage(messageId, resolvedUpdateDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getStatus() == MessageStatus.RESOLVED &&
                            message.getResolvedAt() != null &&
                            message.getResolvedBy().equals(1L) &&
                            message.getResolutionTimeHours() != null &&
                            message.getResolutionTimeHours() > 0
            ));
        }

        @Test
        @DisplayName("Should not change resolved time when message is already resolved")
        void shouldNotChangeResolvedTimeWhenMessageIsAlreadyResolved() {
            // Given
            Long messageId = 1L;
            LocalDateTime originalResolvedTime = LocalDateTime.now().minusHours(1);
            existingMessage.setStatus(MessageStatus.RESOLVED);
            existingMessage.setResolvedAt(originalResolvedTime);

            MessageUpdateDto updateDto = MessageUpdateDto.builder()
                    .status(MessageStatus.RESOLVED)
                    .internalNotes("Additional notes")
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
            when(converterService.mapMessageToDto(existingMessage)).thenReturn(expectedMessageDto);

            // When
            contentService.updateMessage(messageId, updateDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getResolvedAt().equals(originalResolvedTime) // Should remain unchanged
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when message not found")
        void shouldThrowResourceNotFoundExceptionWhenMessageNotFound() {
            // Given
            Long nonExistentMessageId = 999L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(nonExistentMessageId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.updateMessage(nonExistentMessageId, validMessageUpdateDto, request));

            assertEquals("Message not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(messageRepository).findByIdAndIsActiveTrue(nonExistentMessageId);
            verifyNoInteractions(userAccessValidator);
            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate user access to message's school")
        void shouldValidateUserAccessToMessagesSchool() {
            // Given
            Long messageId = 1L;
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.findByIdAndIsActiveTrue(messageId)).thenReturn(Optional.of(existingMessage));
            doThrow(new BusinessException("User does not have access to this school"))
                    .when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.updateMessage(messageId, validMessageUpdateDto, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(messageRepository).findByIdAndIsActiveTrue(messageId);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(messageRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("togglePostLike() Tests")
    class TogglePostLikeTests {

        private Post publishedPost;
        private PostLike existingLike;
        private PostLikeDto expectedLikeDto;

        @BeforeEach
        void setUp() {
            publishedPost = new Post();
            publishedPost.setId(1L);
            publishedPost.setTitle("Published Post");
            publishedPost.setStatus(PostStatus.PUBLISHED);
            publishedPost.setAllowLikes(true);

            existingLike = new PostLike();
            existingLike.setId(1L);
            existingLike.setPost(publishedPost);
            existingLike.setUser(authenticatedUser);
            existingLike.setReactionType(ReactionType.LIKE);

            expectedLikeDto = PostLikeDto.builder()
                    .id(1L)
                    .postId(1L)
                    .userId(1L)
                    .reactionType(ReactionType.LIKE)
                    .build();
        }

        @Test
        @DisplayName("Should create new like when user has not liked post before")
        void shouldCreateNewLikeWhenUserHasNotLikedPostBefore() {
            // Given
            Long postId = 1L;
            ReactionType reactionType = ReactionType.LOVE;

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postLikeRepository.findByPostIdAndUserId(postId, 1L)).thenReturn(Optional.empty());
            when(postLikeRepository.save(any(PostLike.class))).thenReturn(existingLike);
            // when(postRepository.incrementLikeCount(postId)).thenReturn(1); ceyhun

            doNothing().when(postRepository).incrementLikeCount(postId);


            when(converterService.mapPostLikeToDto(existingLike)).thenReturn(expectedLikeDto);

            // When
            PostLikeDto result = contentService.togglePostLike(postId, reactionType, request);

            // Then
            assertNotNull(result);
            assertEquals(ReactionType.LIKE, result.getReactionType());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED);
            verify(postLikeRepository).findByPostIdAndUserId(postId, 1L);
            verify(postLikeRepository).save(argThat(like ->
                    like.getPost().getId().equals(postId) &&
                            like.getUser().getId().equals(1L) &&
                            like.getReactionType() == reactionType &&
                            like.getLikedAt() != null &&
                            like.getCreatedBy().equals(1L)
            ));
            verify(postRepository).incrementLikeCount(postId);
            verify(converterService).mapPostLikeToDto(existingLike);
        }

        @Test
        @DisplayName("Should remove existing like when user has already liked post")
        void shouldRemoveExistingLikeWhenUserHasAlreadyLikedPost() {
            // Given
            Long postId = 1L;
            ReactionType reactionType = ReactionType.LIKE;

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postLikeRepository.findByPostIdAndUserId(postId, 1L)).thenReturn(Optional.of(existingLike));
            doNothing().when(postLikeRepository).delete(existingLike);
           // when(postRepository.decrementLikeCount(postId)).thenReturn(1); ceyhun

            doNothing().when(postRepository).decrementLikeCount(postId);

            // When
            PostLikeDto result = contentService.togglePostLike(postId, reactionType, request);

            // Then
            assertNull(result); // Should return null when unliking

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED);
            verify(postLikeRepository).findByPostIdAndUserId(postId, 1L);
            verify(postLikeRepository).delete(existingLike);
            verify(postRepository).decrementLikeCount(postId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when post not found or not published")
        void shouldThrowResourceNotFoundExceptionWhenPostNotFoundOrNotPublished() {
            // Given
            Long nonExistentPostId = 999L;
            ReactionType reactionType = ReactionType.LIKE;

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.togglePostLike(nonExistentPostId, reactionType, request));

            assertEquals("Post not found or not published", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndStatusAndIsActiveTrue(nonExistentPostId, PostStatus.PUBLISHED);
            verifyNoInteractions(postLikeRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when likes are not allowed on post")
        void shouldThrowBusinessExceptionWhenLikesAreNotAllowedOnPost() {
            // Given
            Long postId = 1L;
            ReactionType reactionType = ReactionType.LIKE;

            Post postWithLikesDisabled = new Post();
            postWithLikesDisabled.setId(postId);
            postWithLikesDisabled.setAllowLikes(false);
            postWithLikesDisabled.setStatus(PostStatus.PUBLISHED);

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(postWithLikesDisabled));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.togglePostLike(postId, reactionType, request));

            assertEquals("Likes are not allowed on this post", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(postRepository).findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED);
            verifyNoInteractions(postLikeRepository);
        }

        @Test
        @DisplayName("Should handle different reaction types")
        void shouldHandleDifferentReactionTypes() {
            // Test all reaction types
            testReactionType(ReactionType.LIKE);
            testReactionType(ReactionType.LOVE);
            testReactionType(ReactionType.WOW);
            testReactionType(ReactionType.CONGRATULATIONS);
            testReactionType(ReactionType.THANKS);
            testReactionType(ReactionType.SUPPORT);
        }

        private void testReactionType(ReactionType reactionType) {
            // Given
            Long postId = 1L;

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(postRepository.findByIdAndStatusAndIsActiveTrue(postId, PostStatus.PUBLISHED))
                    .thenReturn(Optional.of(publishedPost));
            when(postLikeRepository.findByPostIdAndUserId(postId, 1L)).thenReturn(Optional.empty());
            when(postLikeRepository.save(any(PostLike.class))).thenReturn(existingLike);
            // when(postRepository.incrementLikeCount(postId)).thenReturn(1); ceyhun
            doNothing().when(postRepository).incrementLikeCount(postId);
            when(converterService.mapPostLikeToDto(any(PostLike.class))).thenReturn(expectedLikeDto);

            // When
            contentService.togglePostLike(postId, reactionType, request);

            // Then
            verify(postLikeRepository).save(argThat(like ->
                    like.getReactionType() == reactionType
            ));

            reset(jwtService, postRepository, postLikeRepository, converterService);
        }
    }

    @Nested
    @DisplayName("createGallery() Tests")
    class CreateGalleryTests {

        private GalleryCreateDto validGalleryCreateDto;
        private Gallery savedGallery;
        private GalleryDto expectedGalleryDto;

        @BeforeEach
        void setUp() {
            validGalleryCreateDto = GalleryCreateDto.builder()
                    .schoolId(1L)
                    .title("School Events Gallery")
                    .description("Collection of school event photos")
                    .galleryType(GalleryType.EVENTS)
                    .visibility(GalleryVisibility.PUBLIC)
                    .coverImageUrl("https://example.com/cover.jpg")
                    .sortOrder(1)
                    .isFeatured(true)
                    .allowComments(true)
                    .allowDownloads(false)
                    .metaTitle("School Events Gallery")
                    .metaDescription("Collection of photos from school events")
                    .tags("events,school,photos")
                    .build();

            savedGallery = new Gallery();
            savedGallery.setId(1L);
            savedGallery.setSchool(validSchool);
            savedGallery.setTitle("School Events Gallery");
            savedGallery.setSlug("school-events-gallery");
            savedGallery.setCreatedBy(1L);

            expectedGalleryDto = GalleryDto.builder()
                    .id(1L)
                    .title("School Events Gallery")
                    .slug("school-events-gallery")
                    .galleryType(GalleryType.EVENTS)
                    .build();
        }

        @Test
        @DisplayName("Should create gallery for school successfully")
        void shouldCreateGalleryForSchoolSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("School Events Gallery", "gallery"))
                    .thenReturn("school-events-gallery");
            when(galleryRepository.existsBySlug("school-events-gallery")).thenReturn(false);
            when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);
            when(converterService.mapGalleryToDto(savedGallery)).thenReturn(expectedGalleryDto);

            // When
            GalleryDto result = contentService.createGallery(validGalleryCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("School Events Gallery", result.getTitle());
            assertEquals(GalleryType.EVENTS, result.getGalleryType());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            verify(galleryRepository).save(argThat(gallery ->
                    gallery.getSchool().getId().equals(1L) &&
                            gallery.getCampus() == null &&
                            gallery.getBrand() == null &&
                            gallery.getTitle().equals("School Events Gallery") &&
                            gallery.getDescription().equals("Collection of school event photos") &&
                            gallery.getGalleryType() == GalleryType.EVENTS &&
                            gallery.getVisibility() == GalleryVisibility.PUBLIC &&
                            gallery.getIsFeatured().equals(true) &&
                            gallery.getAllowComments().equals(true) &&
                            gallery.getAllowDownloads().equals(false) &&
                            gallery.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapGalleryToDto(savedGallery);
        }

        @Test
        @DisplayName("Should create gallery for campus successfully")
        void shouldCreateGalleryForCampusSuccessfully() {
            // Given
            GalleryCreateDto campusGalleryDto = GalleryCreateDto.builder()
                    .campusId(1L)
                    .title("Campus Facilities")
                    .description("Campus facility photos")
                    .galleryType(GalleryType.FACILITIES)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validCampus));
            doNothing().when(userAccessValidator).validateUserCanAccessCampus(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Campus Facilities", "gallery"))
                    .thenReturn("campus-facilities");
            when(galleryRepository.existsBySlug("campus-facilities")).thenReturn(false);
            when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);
            when(converterService.mapGalleryToDto(any(Gallery.class))).thenReturn(expectedGalleryDto);

            // When
            GalleryDto result = contentService.createGallery(campusGalleryDto, request);

            // Then
            assertNotNull(result);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessCampus(authenticatedUser, 1L);
            verify(galleryRepository).save(argThat(gallery ->
                    gallery.getCampus().getId().equals(1L) &&
                            gallery.getSchool() == null &&
                            gallery.getBrand() == null
            ));
        }

        @Test
        @DisplayName("Should create gallery for brand successfully")
        void shouldCreateGalleryForBrandSuccessfully() {
            // Given
            GalleryCreateDto brandGalleryDto = GalleryCreateDto.builder()
                    .brandId(1L)
                    .title("Brand History")
                    .description("Historical photos of the brand")
                    .galleryType(GalleryType.MIXED)
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validBrand));
            doNothing().when(userAccessValidator).validateUserCanAccessBrand(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Brand History", "gallery"))
                    .thenReturn("brand-history");
            when(galleryRepository.existsBySlug("brand-history")).thenReturn(false);
            when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);
            when(converterService.mapGalleryToDto(any(Gallery.class))).thenReturn(expectedGalleryDto);

            // When
            GalleryDto result = contentService.createGallery(brandGalleryDto, request);

            // Then
            assertNotNull(result);
            verify(brandRepository).findByIdAndIsActiveTrue(1L);
            verify(userAccessValidator).validateUserCanAccessBrand(authenticatedUser, 1L);
            verify(galleryRepository).save(argThat(gallery ->
                    gallery.getBrand().getId().equals(1L) &&
                            gallery.getSchool() == null &&
                            gallery.getCampus() == null
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when no entity is specified")
        void shouldThrowBusinessExceptionWhenNoEntityIsSpecified() {
            // Given
            GalleryCreateDto noEntityDto = GalleryCreateDto.builder()
                    .title("Orphan Gallery")
                    .description("Gallery without entity")
                    // No brandId, campusId, or schoolId specified
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> contentService.createGallery(noEntityDto, request));

            assertEquals("Gallery must belong to a brand, campus, or school", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(schoolRepository, campusRepository, brandRepository);
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            GalleryCreateDto minimalDto = GalleryCreateDto.builder()
                    .schoolId(1L)
                    .title("Minimal Gallery")
                    .description("Basic gallery")
                    .galleryType(GalleryType.PHOTOS)
                    // Optional fields not set
                    .build();

            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("Minimal Gallery", "gallery"))
                    .thenReturn("minimal-gallery");
            when(galleryRepository.existsBySlug("minimal-gallery")).thenReturn(false);
            when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);
            when(converterService.mapGalleryToDto(any(Gallery.class))).thenReturn(expectedGalleryDto);

            // When
            contentService.createGallery(minimalDto, request);

            // Then
            verify(galleryRepository).save(argThat(gallery ->
                    gallery.getVisibility() == GalleryVisibility.PUBLIC && // Default visibility
                            gallery.getSortOrder().equals(0) && // Default sortOrder
                            gallery.getIsFeatured().equals(false) && // Default isFeatured
                            gallery.getAllowComments().equals(true) && // Default allowComments
                            gallery.getAllowDownloads().equals(false) // Default allowDownloads
            ));
        }

        @Test
        @DisplayName("Should handle slug collision")
        void shouldHandleSlugCollision() {
            // Given
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            doNothing().when(userAccessValidator).validateUserCanAccessSchool(authenticatedUser, 1L);
            when(slugGeneratorService.generateUniqueSlug("School Events Gallery", "gallery"))
                    .thenReturn("school-events-gallery");
            when(galleryRepository.existsBySlug("school-events-gallery")).thenReturn(true);
            when(slugGeneratorService.generateUniqueSlug(startsWith("School Events Gallery-"), eq("gallery")))
                    .thenReturn("school-events-gallery-123456789");
            when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);
            when(converterService.mapGalleryToDto(any(Gallery.class))).thenReturn(expectedGalleryDto);

            // When
            contentService.createGallery(validGalleryCreateDto, request);

            // Then
            verify(galleryRepository).existsBySlug("school-events-gallery");
            verify(slugGeneratorService).generateUniqueSlug(startsWith("School Events Gallery-"), eq("gallery"));
            verify(galleryRepository).save(argThat(gallery ->
                    gallery.getSlug().equals("school-events-gallery-123456789")
            ));
        }
    }

    @Nested
    @DisplayName("createMessage() Tests")
    class CreateMessageTests {

        private MessageCreateDto validMessageCreateDto;
        private Message savedMessage;
        private MessageDto expectedMessageDto;

        @BeforeEach
        void setUp() {
            validMessageCreateDto = MessageCreateDto.builder()
                    .schoolId(1L)
                    .senderName("John Doe")
                    .senderEmail("john.doe@example.com")
                    .senderPhone("+90 555 123 4567")
                    .subject("Enrollment Inquiry")
                    .content("I am interested in enrolling my child for the next academic year.")
                    .messageType(MessageType.ENROLLMENT_INQUIRY)
                    .priority(MessagePriority.HIGH)
                    .studentName("Jane Doe")
                    .studentAge(7)
                    .gradeInterested("Grade 2")
                    .enrollmentYear("2024-2025")
                    .preferredContactMethod("email")
                    .preferredContactTime("morning")
                    .requestCallback(true)
                    .requestAppointment(false)
                    .ipAddress("192.168.1.1")
                    .userAgent("Mozilla/5.0")
                    .sourcePage("https://school.com/enrollment")
                    .utmSource("google")
                    .utmMedium("cpc")
                    .utmCampaign("enrollment2024")
                    .attachments("{\"files\": []}")
                    .build();

            savedMessage = new Message();
            savedMessage.setId(1L);
            savedMessage.setSchool(validSchool);
            savedMessage.setSenderName("John Doe");
            savedMessage.setReferenceNumber("uuid-reference");
            savedMessage.setStatus(MessageStatus.NEW);
            savedMessage.setCreatedBy(1L);

            expectedMessageDto = MessageDto.builder()
                    .id(1L)
                    .senderName("John Doe")
                    .subject("Enrollment Inquiry")
                    .messageType(MessageType.ENROLLMENT_INQUIRY)
                    .status(MessageStatus.NEW)
                    .build();
        }

        @Test
        @DisplayName("Should create message for authenticated user successfully")
        void shouldCreateMessageForAuthenticatedUserSuccessfully() {
            // Given
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
            when(converterService.mapMessageToDto(savedMessage)).thenReturn(expectedMessageDto);

            // When
            MessageDto result = contentService.createMessage(validMessageCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("John Doe", result.getSenderName());
            assertEquals("Enrollment Inquiry", result.getSubject());
            assertEquals(MessageType.ENROLLMENT_INQUIRY, result.getMessageType());
            assertEquals(MessageStatus.NEW, result.getStatus());

            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(jwtService).getUser(request);
            verify(messageRepository).save(argThat(message ->
                    message.getSchool().getId().equals(1L) &&
                            message.getSenderName().equals("John Doe") &&
                            message.getSenderEmail().equals("john.doe@example.com") &&
                            message.getSenderPhone().equals("+90 555 123 4567") &&
                            message.getSubject().equals("Enrollment Inquiry") &&
                            message.getContent().equals("I am interested in enrolling my child for the next academic year.") &&
                            message.getMessageType() == MessageType.ENROLLMENT_INQUIRY &&
                            message.getPriority() == MessagePriority.HIGH &&
                            message.getStatus() == MessageStatus.NEW &&
                            message.getStudentName().equals("Jane Doe") &&
                            message.getStudentAge().equals(7) &&
                            message.getGradeInterested().equals("Grade 2") &&
                            message.getEnrollmentYear().equals("2024-2025") &&
                            message.getPreferredContactMethod().equals("email") &&
                            message.getPreferredContactTime().equals("morning") &&
                            message.getRequestCallback().equals(true) &&
                            message.getRequestAppointment().equals(false) &&
                            message.getIpAddress().equals("192.168.1.1") &&
                            message.getUserAgent().equals("Mozilla/5.0") &&
                            message.getSourcePage().equals("https://school.com/enrollment") &&
                            message.getUtmSource().equals("google") &&
                            message.getUtmMedium().equals("cpc") &&
                            message.getUtmCampaign().equals("enrollment2024") &&
                            message.getAttachments().equals("{\"files\": []}") &&
                            message.getHasAttachments().equals(true) &&
                            message.getSenderUser().getId().equals(1L) &&
                            message.getCreatedBy().equals(1L) &&
                            message.getReferenceNumber() != null
            ));
            verify(converterService).mapMessageToDto(savedMessage);
        }

        @Test
        @DisplayName("Should create anonymous message when user not authenticated")
        void shouldCreateAnonymousMessageWhenUserNotAuthenticated() {
            // Given
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(jwtService.getUser(request)).thenThrow(new RuntimeException("User not authenticated"));
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
            when(converterService.mapMessageToDto(savedMessage)).thenReturn(expectedMessageDto);

            // When
            MessageDto result = contentService.createMessage(validMessageCreateDto, request);

            // Then
            assertNotNull(result);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(jwtService).getUser(request);
            verify(messageRepository).save(argThat(message ->
                    message.getSenderUser() == null && // Anonymous message
                            message.getCreatedBy() == null &&
                            message.getSenderName().equals("John Doe") &&
                            message.getReferenceNumber() != null
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            Long nonExistentSchoolId = 999L;
            MessageCreateDto invalidSchoolDto = MessageCreateDto.builder()
                    .schoolId(nonExistentSchoolId)
                    .senderName("John Doe")
                    .subject("Test Message")
                    .content("Test content")
                    .build();

            when(schoolRepository.findByIdAndIsActiveTrue(nonExistentSchoolId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> contentService.createMessage(invalidSchoolDto, request));

            assertEquals("School not found", exception.getMessage());

            verify(schoolRepository).findByIdAndIsActiveTrue(nonExistentSchoolId);
            verifyNoInteractions(messageRepository, converterService);
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            MessageCreateDto minimalDto = MessageCreateDto.builder()
                    .schoolId(1L)
                    .senderName("Jane Smith")
                    .senderEmail("jane@example.com")
                    .subject("General Inquiry")
                    .content("General question")
                    // Optional fields not set
                    .build();

            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
            when(converterService.mapMessageToDto(any(Message.class))).thenReturn(expectedMessageDto);

            // When
            contentService.createMessage(minimalDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getPriority() == MessagePriority.NORMAL && // Default priority
                            message.getStatus() == MessageStatus.NEW && // Default status
                            message.getRequestCallback().equals(false) && // Default requestCallback
                            message.getRequestAppointment().equals(false) && // Default requestAppointment
                            message.getHasAttachments().equals(false) // Default hasAttachments (no attachments)
            ));
        }

        @Test
        @DisplayName("Should detect attachments correctly")
        void shouldDetectAttachmentsCorrectly() {
            // Test with attachments
            MessageCreateDto withAttachmentsDto = MessageCreateDto.builder()
                    .schoolId(1L)
                    .senderName("John Doe")
                    .senderEmail("john@example.com")
                    .subject("Message with attachments")
                    .content("Content")
                    .attachments("{\"files\": [{\"name\": \"document.pdf\"}]}")
                    .build();

            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
            when(converterService.mapMessageToDto(any(Message.class))).thenReturn(expectedMessageDto);

            // When
            contentService.createMessage(withAttachmentsDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getAttachments().equals("{\"files\": [{\"name\": \"document.pdf\"}]}") &&
                            message.getHasAttachments().equals(true)
            ));

            // Test without attachments
            reset(messageRepository);

            MessageCreateDto noAttachmentsDto = MessageCreateDto.builder()
                    .schoolId(1L)
                    .senderName("John Doe")
                    .senderEmail("john@example.com")
                    .subject("Message without attachments")
                    .content("Content")
                    .attachments(null)
                    .build();

            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

            // When
            contentService.createMessage(noAttachmentsDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getAttachments() == null &&
                            message.getHasAttachments().equals(false)
            ));
        }

        @Test
        @DisplayName("Should generate unique reference number")
        void shouldGenerateUniqueReferenceNumber() {
            // Given
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(jwtService.getUser(request)).thenReturn(authenticatedUser);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
            when(converterService.mapMessageToDto(any(Message.class))).thenReturn(expectedMessageDto);

            // When
            contentService.createMessage(validMessageCreateDto, request);

            // Then
            verify(messageRepository).save(argThat(message ->
                    message.getReferenceNumber() != null &&
                            message.getReferenceNumber().length() > 0
            ));
        }
    }
}