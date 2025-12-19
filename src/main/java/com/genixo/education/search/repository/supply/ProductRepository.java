package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.Product;
import com.genixo.education.search.enumaration.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySupplierId(Long supplierId, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier.id = :supplierId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("searchTerm") String searchTerm,
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("status") ProductStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.supplier.id = :supplierId")
    Long countBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.supplier.id = :supplierId AND p.status = 'ACTIVE'")
    Long countActiveBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    Boolean existsBySku(String sku);

    Boolean existsBySkuAndIdNot(String sku, Long id);
}
