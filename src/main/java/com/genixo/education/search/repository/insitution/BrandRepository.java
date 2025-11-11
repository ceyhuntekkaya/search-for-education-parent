package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.dto.institution.BrandSummaryDto;
import com.genixo.education.search.entity.institution.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b WHERE b.isActive = true AND b.id = :id")
    Optional<Brand> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT b FROM Brand b WHERE b.isActive = true AND LOWER(b.slug) = LOWER(:slug)")
    Optional<Brand> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Brand b WHERE LOWER(b.name) = LOWER(:name) AND b.isActive = true")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Brand b WHERE LOWER(b.name) = LOWER(:name) AND b.id != :id AND b.isActive = true")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Brand b WHERE b.slug = :slug AND b.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT b FROM Brand b WHERE b.isActive = true ORDER BY b.name ASC")
    List<Brand> findAllActiveOrderByName();

    @Query("SELECT b FROM Brand b WHERE b.id IN :ids AND b.isActive = true ORDER BY b.name ASC")
    List<Brand> findByIdInAndIsActiveTrueOrderByName(@Param("ids") List<Long> ids);

    @Query("SELECT b FROM Brand b " +
            "WHERE b.isActive = true " +
            "AND b.slug = :slug " +
            "AND EXISTS (SELECT 1 FROM Campus c WHERE c.brand.id = b.id AND c.isActive = true AND c.isSubscribed = true)")
    Optional<Brand> findBySlugAndIsActiveTrueWithSubscribedCampuses(@Param("slug") String slug);

    @Modifying
    @Query("UPDATE Brand b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);




    @Query("SELECT b FROM Brand b WHERE b.isActive = true AND " +
            "(:searchTerm IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Brand> searchBrands(@Param("searchTerm") String searchTerm);

    @Query("SELECT b.id FROM Brand b WHERE b.isActive = true")
    List<Long> findAllActiveBrandIds();
}