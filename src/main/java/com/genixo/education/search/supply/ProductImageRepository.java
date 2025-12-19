package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    @Query("SELECT MAX(pi.displayOrder) FROM ProductImage pi WHERE pi.product.id = :productId")
    Integer findMaxDisplayOrderByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
}

