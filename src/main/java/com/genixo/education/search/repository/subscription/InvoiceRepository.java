package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.entity.subscription.Invoice;
import com.genixo.education.search.enumaration.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT i FROM Invoice i WHERE i.isActive = true AND i.id = :id")
    Optional<Invoice> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber AND i.isActive = true")
    Optional<Invoice> findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT i FROM Invoice i WHERE i.subscription.id = :subscriptionId AND i.isActive = true ORDER BY i.invoiceDate DESC")
    Page<Invoice> findBySubscriptionIdOrderByInvoiceDateDesc(@Param("subscriptionId") Long subscriptionId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.payment.id = :paymentId AND i.isActive = true")
    Optional<Invoice> findByPaymentId(@Param("paymentId") Long paymentId);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus = :status AND i.isActive = true")
    List<Invoice> findByInvoiceStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate <= :date AND i.invoiceStatus IN ('SENT', 'VIEWED') AND i.isActive = true")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDateTime date);

    @Query("SELECT i FROM Invoice i WHERE i.subscription.campus.id = :campusId AND i.isActive = true ORDER BY i.invoiceDate DESC")
    Page<Invoice> findByCampusIdOrderByInvoiceDateDesc(@Param("campusId") Long campusId, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceNumber LIKE :prefix AND i.isActive = true")
    Long countByInvoiceNumberPrefix(@Param("prefix") String prefix);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate AND i.isActive = true ORDER BY i.invoiceDate DESC")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.invoiceStatus = 'PAID' AND i.invoiceDate >= :fromDate AND i.isActive = true")
    BigDecimal getTotalPaidAmountSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceStatus = 'PAID' AND i.invoiceDate >= :fromDate AND i.isActive = true")
    Long getPaidInvoiceCountSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT i FROM Invoice i WHERE i.eInvoiceStatus IS NOT NULL AND i.eInvoiceStatus != 'SENT' AND i.isActive = true")
    List<Invoice> findInvoicesWithPendingEInvoice();

    // Tax reporting queries
    @Query("SELECT i FROM Invoice i WHERE YEAR(i.invoiceDate) = :year AND MONTH(i.invoiceDate) = :month AND i.invoiceStatus = 'PAID' AND i.isActive = true")
    List<Invoice> findPaidInvoicesByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(i.taxAmount) FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate AND i.invoiceStatus = 'PAID' AND i.isActive = true")
    BigDecimal getTotalTaxAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}