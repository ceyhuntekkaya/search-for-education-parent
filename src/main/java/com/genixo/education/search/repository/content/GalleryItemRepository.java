package com.genixo.education.search.repository.content;

import com.genixo.education.search.entity.content.GalleryItem;
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
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.isActive = true AND gi.id = :id")
    Optional<GalleryItem> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isActive = true " +
            "ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    List<GalleryItem> findByGalleryIdAndIsActiveTrue(@Param("galleryId") Long galleryId);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isActive = true " +
            "ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    Page<GalleryItem> findByGalleryIdAndIsActiveTrue(@Param("galleryId") Long galleryId, Pageable pageable);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.itemType = :itemType " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC, gi.createdAt DESC")
    List<GalleryItem> findByGalleryIdAndItemTypeAndIsActiveTrue(@Param("galleryId") Long galleryId, @Param("itemType") MediaType itemType);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isFeatured = true " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC")
    List<GalleryItem> findFeaturedItemsByGalleryId(@Param("galleryId") Long galleryId);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isCover = true " +
            "AND gi.isActive = true ORDER BY gi.sortOrder ASC")
    Optional<GalleryItem> findCoverItemByGalleryId(@Param("galleryId") Long galleryId);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.processingStatus = :status AND gi.isActive = true")
    List<GalleryItem> findByProcessingStatus(@Param("status") ProcessingStatus status);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.isFlagged = true AND gi.isActive = true")
    List<GalleryItem> findFlaggedItems();

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.isModerated = false AND gi.isActive = true " +
            "ORDER BY gi.createdAt ASC")
    List<GalleryItem> findUnmoderatedItems();

    @Modifying
    @Query("UPDATE GalleryItem gi SET gi.viewCount = gi.viewCount + 1 WHERE gi.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE GalleryItem gi SET gi.downloadCount = gi.downloadCount + 1 WHERE gi.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE GalleryItem gi SET gi.likeCount = gi.likeCount + 1 WHERE gi.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE GalleryItem gi SET gi.likeCount = CASE WHEN gi.likeCount > 0 THEN gi.likeCount - 1 ELSE 0 END WHERE gi.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Query("SELECT COUNT(gi) FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isActive = true")
    long countByGalleryId(@Param("galleryId") Long galleryId);

    @Query("SELECT SUM(gi.fileSizeBytes) FROM GalleryItem gi WHERE gi.gallery.id = :galleryId AND gi.isActive = true")
    Long getTotalSizeByGalleryId(@Param("galleryId") Long galleryId);

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.uploadedByUser.id = :userId AND gi.isActive = true " +
            "ORDER BY gi.createdAt DESC")
    List<GalleryItem> findByUploadedByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    @Query("SELECT DISTINCT gi.tags FROM GalleryItem gi WHERE gi.isActive = true AND gi.tags IS NOT NULL")
    List<String> findDistinctTags();

    @Query("SELECT gi FROM GalleryItem gi WHERE gi.gallery.id = :galleryId ")
    List<GalleryItem> findByGalleryId(@Param("galleryId") Long galleryId);

    @Modifying
    @Query("DELETE FROM GalleryItem gi WHERE gi.gallery.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);
}
