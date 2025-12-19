package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    List<ProductDiscount> findByProductId(Long productId);

    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.product.id = :productId AND " +
           "pd.isActive = true AND " +
           "(pd.startDate IS NULL OR pd.startDate <= :currentDate) AND " +
           "(pd.endDate IS NULL OR pd.endDate >= :currentDate)")
    List<ProductDiscount> findActiveDiscountsByProductId(
            @Param("productId") Long productId,
            @Param("currentDate") LocalDate currentDate
    );
}

