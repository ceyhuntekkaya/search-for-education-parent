package com.genixo.education.search.repository.content;

import com.genixo.education.search.dto.content.SocialMediaAnalyticsDto;
import com.genixo.education.search.entity.content.Post;
import com.genixo.education.search.enumaration.PostStatus;
import com.genixo.education.search.enumaration.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isActive = true AND p.id = :id")
    Optional<Post> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT p FROM Post p WHERE p.isActive = true AND LOWER(p.slug) = LOWER(:slug) AND p.status = :status")
    Optional<Post> findBySlugAndStatusAndIsActiveTrue(@Param("slug") String slug, @Param("status") PostStatus status);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Post p WHERE p.slug = :slug AND p.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT p FROM Post p WHERE p.school.id = :schoolId AND p.status = :status AND p.isActive = true " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findBySchoolIdAndStatusAndIsActiveTrueOrderByPublishedAtDesc(
            @Param("schoolId") Long schoolId,
            @Param("status") PostStatus status,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.school.campus.isSubscribed = true AND p.isActive = true " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findByStatusAndSchoolCampusIsSubscribedTrueAndIsActiveTrueOrderByPublishedAtDesc(
            @Param("status") PostStatus status,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Post p WHERE p.school.id = :schoolId AND p.school.campus.isSubscribed = true AND p.isActive = true")
    boolean existsBySchoolIdAndIsActiveTrueAndCampusIsSubscribedTrue(@Param("schoolId") Long schoolId);

    // Complex search query with multiple filters
    @Query("SELECT DISTINCT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(p.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:schoolId IS NULL OR p.school.id = :schoolId) " +
            "AND (:accessibleSchoolIds IS NULL OR p.school.id IN :accessibleSchoolIds) " +
            "AND (:authorId IS NULL OR p.authorUser.id = :authorId) " +
            "AND (:postType IS NULL OR p.postType = :postType) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:isFeatured IS NULL OR p.isFeatured = :isFeatured) " +
            "AND (:isPinned IS NULL OR p.isPinned = :isPinned) " +
            "AND (:publishedAfter IS NULL OR p.publishedAt >= :publishedAfter) " +
            "AND (:publishedBefore IS NULL OR p.publishedAt <= :publishedBefore) " +
            "AND (:tags IS NULL OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) " +
            "AND (:hashtags IS NULL OR LOWER(p.hashtags) LIKE LOWER(CONCAT('%', :hashtags, '%')))")
    Page<Post> searchPosts(
            @Param("searchTerm") String searchTerm,
            @Param("schoolId") Long schoolId,
            @Param("accessibleSchoolIds") List<Long> accessibleSchoolIds,
            @Param("authorId") Long authorId,
            @Param("postType") PostType postType,
            @Param("status") PostStatus status,
            @Param("isFeatured") Boolean isFeatured,
            @Param("isPinned") Boolean isPinned,
            @Param("publishedAfter") LocalDateTime publishedAfter,
            @Param("publishedBefore") LocalDateTime publishedBefore,
            @Param("tags") String tags,
            @Param("hashtags") String hashtags,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = CASE WHEN p.likeCount > 0 THEN p.likeCount - 1 ELSE 0 END WHERE p.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = CASE WHEN p.commentCount > 0 THEN p.commentCount - 1 ELSE 0 END WHERE p.id = :id")
    void decrementCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.shareCount = p.shareCount + 1 WHERE p.id = :id")
    void incrementShareCount(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.school.id = :schoolId AND p.createdAt >= :startOfMonth AND p.isActive = true")
    long countMonthlyPostsBySchool(@Param("schoolId") Long schoolId, @Param("startOfMonth") LocalDateTime startOfMonth);

    @Query("SELECT p FROM Post p WHERE p.school.id = :schoolId AND p.status = 'PUBLISHED' AND p.isActive = true " +
            "ORDER BY (p.likeCount + p.commentCount + p.shareCount) DESC")
    List<Post> findTopPostsByEngagement(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.school.id = :schoolId AND p.status = 'PUBLISHED' AND p.isActive = true " +
            "AND p.publishedAt >= :date ORDER BY p.viewCount DESC")
    List<Post> findTrendingPostsBySchoolAndDate(@Param("schoolId") Long schoolId, @Param("date") LocalDateTime date, Pageable pageable);

    // Social Media Analytics Query
    @Query("SELECT new com.genixo.education.search.dto.content.SocialMediaAnalyticsDto(" +
            "s.id, s.name, :date, " +
            "COALESCE((SELECT COUNT(p) FROM Post p WHERE p.school.id = s.id AND p.isActive = true), 0L), " +
            "COALESCE((SELECT COUNT(p) FROM Post p WHERE p.school.id = s.id AND p.status = 'PUBLISHED' AND p.isActive = true), 0L), " +
            "COALESCE((SELECT COUNT(p) FROM Post p WHERE p.school.id = s.id AND p.status = 'SCHEDULED' AND p.isActive = true), 0L), " +
            "COALESCE((SELECT COUNT(p) FROM Post p WHERE p.school.id = s.id AND p.status = 'DRAFT' AND p.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(p.likeCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(p.commentCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(p.shareCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(p.viewCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true), 0L), " +
            "CASE WHEN (SELECT SUM(p.viewCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true) > 0 " +
            "THEN CAST((SELECT SUM(p.likeCount + p.commentCount + p.shareCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true) AS DOUBLE) / " +
            "     CAST((SELECT SUM(p.viewCount) FROM Post p WHERE p.school.id = s.id AND p.isActive = true) AS DOUBLE) * 100 " +
            "ELSE 0.0 END, " +
            "0L, 0L, 0L, null, null, " +
            "COALESCE((SELECT COUNT(g) FROM Gallery g WHERE g.school.id = s.id AND g.isActive = true), 0L), " +
            "COALESCE((SELECT COUNT(gi) FROM GalleryItem gi WHERE gi.gallery.school.id = s.id AND gi.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(g.viewCount) FROM Gallery g WHERE g.school.id = s.id AND g.isActive = true), 0L), " +
            "COALESCE((SELECT SUM(g.downloadCount) FROM Gallery g WHERE g.school.id = s.id AND g.isActive = true), 0L)) " +
            "FROM School s WHERE s.id = :schoolId AND s.isActive = true")
    SocialMediaAnalyticsDto getSocialMediaAnalytics(@Param("schoolId") Long schoolId, @Param("date") LocalDate date);

    @Query("SELECT p FROM Post p WHERE p.status = 'SCHEDULED' AND p.scheduledAt <= :now AND p.isActive = true")
    List<Post> findScheduledPostsToPublish(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Post p WHERE p.isPinned = true AND p.pinExpiresAt IS NOT NULL AND p.pinExpiresAt <= :now AND p.isActive = true")
    List<Post> findExpiredPinnedPosts(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Post p WHERE p.expiresAt IS NOT NULL AND p.expiresAt <= :now AND p.status = 'PUBLISHED' AND p.isActive = true")
    List<Post> findExpiredPosts(@Param("now") LocalDateTime now);

    Optional<Post> findByIdAndStatusAndIsActiveTrue(Long postId, PostStatus postStatus);
}
