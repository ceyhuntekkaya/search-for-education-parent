package com.genixo.education.search.repository.content;


import com.genixo.education.search.entity.content.PostItem;
import com.genixo.education.search.enumaration.MediaType;
import com.genixo.education.search.enumaration.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostItemRepository extends JpaRepository<PostItem, Long> {

    @Query("SELECT gi FROM PostItem gi WHERE gi.isActive = true AND gi.id = :id")
    Optional<PostItem> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId AND gi.isActive = true " +
            "ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    List<PostItem> findByPostIdAndIsActiveTrue(@Param("postId") Long postId);

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId AND gi.isActive = true " +
            "ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    Page<PostItem> findByPostIdAndIsActiveTrue(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId AND gi.itemType = :itemType " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    List<PostItem> findByPostIdAndItemTypeAndIsActiveTrue(@Param("postId") Long postId, @Param("itemType") MediaType itemType);

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId AND gi.isFeatured = true " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC")
    List<PostItem> findFeaturedItemsByPostId(@Param("postId") Long postId);

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId AND gi.isCover = true " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC")
    Optional<PostItem> findCoverItemByPostId(@Param("postId") Long postId);

    @Query("SELECT gi FROM PostItem gi WHERE gi.processingStatus = :status AND gi.isActive = true")
    List<PostItem> findByProcessingStatus(@Param("status") ProcessingStatus status);

    @Query("SELECT gi FROM PostItem gi WHERE gi.isFlagged = true AND gi.isActive = true")
    List<PostItem> findFlaggedItems();

    @Query("SELECT gi FROM PostItem gi WHERE gi.isModerated = false AND gi.isActive = true " +
            "ORDER BY gi.createdAt ASC")
    List<PostItem> findUnmoderatedItems();

    @Modifying
    @Query("UPDATE PostItem gi SET gi.viewCount = gi.viewCount + 1 WHERE gi.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostItem gi SET gi.downloadCount = gi.downloadCount + 1 WHERE gi.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostItem gi SET gi.likeCount = gi.likeCount + 1 WHERE gi.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostItem gi SET gi.likeCount = CASE WHEN gi.likeCount > 0 THEN gi.likeCount - 1 ELSE 0 END WHERE gi.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Query("SELECT COUNT(gi) FROM PostItem gi WHERE gi.post.id = :postId AND gi.isActive = true")
    long countByPostId(@Param("postId") Long postId);

    @Query("SELECT SUM(gi.fileSizeBytes) FROM PostItem gi WHERE gi.post.id = :postId AND gi.isActive = true")
    Long getTotalSizeByPostId(@Param("postId") Long postId);

    @Query("SELECT gi FROM PostItem gi WHERE gi.uploadedByUser.id = :userId AND gi.isActive = true " +
            "ORDER BY gi.createdAt DESC")
    List<PostItem> findByUploadedByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    @Query("SELECT DISTINCT gi.tags FROM PostItem gi WHERE gi.isActive = true AND gi.tags IS NOT NULL")
    List<String> findDistinctTags();

    @Query("SELECT gi FROM PostItem gi WHERE gi.post.id = :postId ")
    List<PostItem> findByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostItem gi WHERE gi.post.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);
}
