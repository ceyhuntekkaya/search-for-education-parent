package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {

    List<CategoryAttribute> findByCategoryId(Long categoryId);

    List<CategoryAttribute> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);

    @Query("SELECT MAX(ca.displayOrder) FROM CategoryAttribute ca WHERE ca.category.id = :categoryId")
    Integer findMaxDisplayOrderByCategoryId(@Param("categoryId") Long categoryId);

    Optional<CategoryAttribute> findByCategoryIdAndAttributeName(Long categoryId, String attributeName);

    Boolean existsByCategoryIdAndAttributeName(Long categoryId, String attributeName);

    Boolean existsByCategoryIdAndAttributeNameAndIdNot(Long categoryId, String attributeName, Long id);
}

