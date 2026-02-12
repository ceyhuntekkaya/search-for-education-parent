package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.Product;
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

    @Query(value = "SELECT p.* FROM products p WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(p.name::text) LIKE LOWER('%' || :searchTerm || '%') OR " +
           "LOWER(p.sku::text) LIKE LOWER('%' || :searchTerm || '%') OR " +
           "LOWER(p.description::text) LIKE LOWER('%' || :searchTerm || '%')) AND " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier_id = :supplierId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.base_price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.base_price <= :maxPrice)",
           countQuery = "SELECT COUNT(*) FROM products p WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(p.name::text) LIKE LOWER('%' || :searchTerm || '%') OR " +
           "LOWER(p.sku::text) LIKE LOWER('%' || :searchTerm || '%') OR " +
           "LOWER(p.description::text) LIKE LOWER('%' || :searchTerm || '%')) AND " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:supplierId IS NULL OR p.supplier_id = :supplierId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.base_price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.base_price <= :maxPrice)",
           nativeQuery = true)
    Page<Product> searchProducts(
            @Param("searchTerm") String searchTerm,
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
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
