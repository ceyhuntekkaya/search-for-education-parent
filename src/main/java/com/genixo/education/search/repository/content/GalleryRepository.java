package com.genixo.education.search.repository.content;

import com.genixo.education.search.entity.content.Gallery;
import com.genixo.education.search.enumaration.GalleryType;
import com.genixo.education.search.enumaration.GalleryVisibility;
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
public interface GalleryRepository  extends JpaRepository<Gallery, Long> {

    @Query("SELECT g FROM Gallery g WHERE g.isActive = true AND g.id = :id")
    Optional<Gallery> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT g FROM Gallery g WHERE g.isActive = true AND LOWER(g.slug) = LOWER(:slug)")
    Optional<Gallery> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
            "FROM Gallery g WHERE g.slug = :slug AND g.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT g FROM Gallery g WHERE g.school.id = :schoolId AND g.isActive = true ORDER BY g.sortOrder ASC, g.createdAt DESC")
    List<Gallery> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId);

    @Query("SELECT g FROM Gallery g WHERE g.campus.id = :campusId AND g.isActive = true ORDER BY g.sortOrder ASC, g.createdAt DESC")
    List<Gallery> findByCampusIdAndIsActiveTrue(@Param("campusId") Long campusId);

    @Query("SELECT g FROM Gallery g WHERE g.brand.id = :brandId AND g.isActive = true ORDER BY g.sortOrder ASC, g.createdAt DESC")
    List<Gallery> findByBrandIdAndIsActiveTrue(@Param("brandId") Long brandId);

    // Complex search query
    @Query("SELECT DISTINCT g FROM Gallery g " +
            "WHERE g.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(g.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(g.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:brandId IS NULL OR g.brand.id = :brandId) " +
            "AND (:campusId IS NULL OR g.campus.id = :campusId) " +
            "AND (:schoolId IS NULL OR g.school.id = :schoolId) " +
            "AND (:accessibleBrandIds IS NULL OR g.brand.id IN :accessibleBrandIds OR g.brand IS NULL) " +
            "AND (:accessibleCampusIds IS NULL OR g.campus.id IN :accessibleCampusIds OR g.campus IS NULL) " +
            "AND (:accessibleSchoolIds IS NULL OR g.school.id IN :accessibleSchoolIds OR g.school IS NULL) " +
            "AND (:galleryType IS NULL OR g.galleryType = :galleryType) " +
            "AND (:visibility IS NULL OR g.visibility = :visibility) " +
            "AND (:isFeatured IS NULL OR g.isFeatured = :isFeatured) " +
            "AND (:tags IS NULL OR LOWER(g.tags) LIKE LOWER(CONCAT('%', :tags, '%')))")
    Page<Gallery> searchGalleries(
            @Param("searchTerm") String searchTerm,
            @Param("brandId") Long brandId,
            @Param("campusId") Long campusId,
            @Param("schoolId") Long schoolId,
            @Param("accessibleBrandIds") List<Long> accessibleBrandIds,
            @Param("accessibleCampusIds") List<Long> accessibleCampusIds,
            @Param("accessibleSchoolIds") List<Long> accessibleSchoolIds,
            @Param("galleryType") GalleryType galleryType,
            @Param("visibility") GalleryVisibility visibility,
            @Param("isFeatured") Boolean isFeatured,
            @Param("tags") String tags,
            Pageable pageable);

    @Query("SELECT g FROM Gallery g " +
            "WHERE g.isActive = true AND g.visibility = :visibility " +
            "AND (:schoolId IS NULL OR g.school.id = :schoolId) " +
            "AND (:galleryType IS NULL OR g.galleryType = :galleryType) " +
            "AND (g.school IS NULL OR g.school.campus.isSubscribed = true)")
    Page<Gallery> findPublicGalleries(
            @Param("schoolId") Long schoolId,
            @Param("galleryType") GalleryType galleryType,
            @Param("visibility") GalleryVisibility visibility,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Gallery g SET g.itemCount = g.itemCount + 1 WHERE g.id = :id")
    void incrementItemCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Gallery g SET g.itemCount = CASE WHEN g.itemCount > 0 THEN g.itemCount - 1 ELSE 0 END WHERE g.id = :id")
    void decrementItemCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Gallery g SET g.viewCount = g.viewCount + 1 WHERE g.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Gallery g SET g.downloadCount = g.downloadCount + 1 WHERE g.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Gallery g SET g.totalSizeBytes = g.totalSizeBytes + :sizeBytes WHERE g.id = :id")
    void addToTotalSize(@Param("id") Long id, @Param("sizeBytes") Long sizeBytes);

    @Modifying
    @Query("UPDATE Gallery g SET g.totalSizeBytes = CASE WHEN g.totalSizeBytes >= :sizeBytes THEN g.totalSizeBytes - :sizeBytes ELSE 0 END WHERE g.id = :id")
    void subtractFromTotalSize(@Param("id") Long id, @Param("sizeBytes") Long sizeBytes);

    @Query("SELECT g FROM Gallery g WHERE g.isFeatured = true AND g.isActive = true " +
            "AND g.visibility = 'PUBLIC' ORDER BY g.sortOrder ASC, g.createdAt DESC")
    List<Gallery> findFeaturedPublicGalleries(Pageable pageable);
}
