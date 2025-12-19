package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentId(Long parentId);

    List<Category> findByParentIdOrderByDisplayOrderAsc(Long parentId);

    List<Category> findByParentIsNull();

    List<Category> findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = :isActive ORDER BY c.displayOrder ASC")
    List<Category> findRootCategories(@Param("isActive") Boolean isActive);

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.isActive = :isActive ORDER BY c.displayOrder ASC")
    List<Category> findSubCategoriesByParentId(@Param("parentId") Long parentId, @Param("isActive") Boolean isActive);

    @Query("SELECT c FROM Category c WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive) AND " +
           "(:parentId IS NULL OR c.parent.id = :parentId)")
    Page<Category> searchCategories(
            @Param("searchTerm") String searchTerm,
            @Param("isActive") Boolean isActive,
            @Param("parentId") Long parentId,
            Pageable pageable
    );

    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parent.id = :parentId")
    Integer findMaxDisplayOrderByParentId(@Param("parentId") Long parentId);

    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parent IS NULL")
    Integer findMaxDisplayOrderForRootCategories();

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND " +
           "(:parentId IS NULL AND c.parent IS NULL OR :parentId IS NOT NULL AND c.parent.id = :parentId)")
    Boolean existsByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.id != :id AND " +
           "(:parentId IS NULL AND c.parent IS NULL OR :parentId IS NOT NULL AND c.parent.id = :parentId)")
    Boolean existsByNameAndParentIdAndIdNot(@Param("name") String name, @Param("parentId") Long parentId, @Param("id") Long id);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
    Long countByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId AND c.isActive = true")
    Long countActiveByParentId(@Param("parentId") Long parentId);
}

