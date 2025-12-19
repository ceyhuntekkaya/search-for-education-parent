package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.Quotation;
import com.genixo.education.search.enumaration.QuotationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByRfqId(Long rfqId);

    Page<Quotation> findBySupplierId(Long supplierId, Pageable pageable);

    @Query("SELECT q FROM Quotation q WHERE q.rfq.company.id = :companyId")
    Page<Quotation> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT q FROM Quotation q WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(q.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:rfqId IS NULL OR q.rfq.id = :rfqId) AND " +
           "(:supplierId IS NULL OR q.supplier.id = :supplierId) AND " +
           "(:companyId IS NULL OR q.rfq.company.id = :companyId) AND " +
           "(:status IS NULL OR q.status = :status)")
    Page<Quotation> searchQuotations(
            @Param("searchTerm") String searchTerm,
            @Param("rfqId") Long rfqId,
            @Param("supplierId") Long supplierId,
            @Param("companyId") Long companyId,
            @Param("status") QuotationStatus status,
            Pageable pageable
    );

    @Query("SELECT q FROM Quotation q WHERE q.rfq.id = :rfqId AND q.supplier.id = :supplierId ORDER BY q.versionNumber DESC")
    List<Quotation> findByRfqIdAndSupplierIdOrderByVersionNumberDesc(
            @Param("rfqId") Long rfqId,
            @Param("supplierId") Long supplierId
    );

    @Query("SELECT MAX(q.versionNumber) FROM Quotation q WHERE q.rfq.id = :rfqId AND q.supplier.id = :supplierId")
    Integer findMaxVersionNumberByRfqIdAndSupplierId(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.supplier.id = :supplierId")
    Long countBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.supplier.id = :supplierId AND q.status = :status")
    Long countBySupplierIdAndStatus(@Param("supplierId") Long supplierId, @Param("status") QuotationStatus status);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.rfq.company.id = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.rfq.company.id = :companyId AND q.status = :status")
    Long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") QuotationStatus status);

    @Query("SELECT COALESCE(AVG(q.totalAmount), 0) FROM Quotation q WHERE q.supplier.id = :supplierId")
    BigDecimal avgTotalAmountBySupplierId(@Param("supplierId") Long supplierId);

    @Query(value = "SELECT AVG(DATEDIFF(q.created_at, r.created_at)) FROM quotations q JOIN rfqs r ON q.rfq_id = r.id WHERE q.supplier_id = :supplierId", nativeQuery = true)
    Long avgResponseTimeDaysBySupplierId(@Param("supplierId") Long supplierId);

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(HOUR, r.created_at, q.created_at)) FROM quotations q JOIN rfqs r ON q.rfq_id = r.id WHERE q.supplier_id = :supplierId", nativeQuery = true)
    BigDecimal avgResponseTimeHoursBySupplierId(@Param("supplierId") Long supplierId);

}

