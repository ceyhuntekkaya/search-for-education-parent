package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.SupplierRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRatingRepository extends JpaRepository<SupplierRating, Long> {

    List<SupplierRating> findBySupplierId(Long supplierId);

    Page<SupplierRating> findBySupplierId(Long supplierId, Pageable pageable);

    Optional<SupplierRating> findByOrderId(Long orderId);

    @Query("SELECT AVG((r.deliveryRating + r.qualityRating + r.communicationRating) / 3.0) " +
           "FROM SupplierRating r WHERE r.supplier.id = :supplierId")
    Double calculateAverageRating(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(r) FROM SupplierRating r WHERE r.supplier.id = :supplierId")
    Long countBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT AVG(r.deliveryRating) FROM SupplierRating r WHERE r.supplier.id = :supplierId")
    BigDecimal avgDeliveryRatingBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT AVG(r.qualityRating) FROM SupplierRating r WHERE r.supplier.id = :supplierId")
    BigDecimal avgQualityRatingBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT AVG(r.communicationRating) FROM SupplierRating r WHERE r.supplier.id = :supplierId")
    BigDecimal avgCommunicationRatingBySupplierId(@Param("supplierId") Long supplierId);
}

