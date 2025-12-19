package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.entity.subscription.Payment;
import com.genixo.education.search.enumaration.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.isActive = true AND p.id = :id")
    Optional<Payment> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT p FROM Payment p WHERE p.externalPaymentId = :externalPaymentId AND p.isActive = true")
    Optional<Payment> findByExternalPaymentId(@Param("externalPaymentId") String externalPaymentId);

    @Query("SELECT p FROM Payment p WHERE p.paymentReference = :reference AND p.isActive = true")
    Optional<Payment> findByPaymentReference(@Param("reference") String reference);

    @Query("SELECT p FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.isActive = true ORDER BY p.createdAt DESC")
    Page<Payment> findBySubscriptionIdOrderByCreatedAtDesc(@Param("subscriptionId") Long subscriptionId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.paymentStatus = :status AND p.isActive = true")
    List<Payment> findBySubscriptionIdAndPaymentStatus(@Param("subscriptionId") Long subscriptionId, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.dueDate <= :date AND p.isActive = true")
    List<Payment> findOverduePayments(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'FAILED' AND p.createdAt >= :since AND p.isActive = true")
    List<Payment> findFailedPaymentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.paymentStatus = 'COMPLETED' AND p.isActive = true")
    BigDecimal getTotalPaidBySubscription(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.paymentStatus = 'COMPLETED' AND p.isActive = true")
    Long getSuccessfulPaymentCount(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.paymentStatus = 'FAILED' AND p.isActive = true")
    Long getFailedPaymentCount(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT p FROM Payment p WHERE p.subscription.campus.id = :campusId AND p.isActive = true ORDER BY p.createdAt DESC")
    Page<Payment> findByCampusIdOrderByCreatedAtDesc(@Param("campusId") Long campusId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus IN :statuses AND p.paymentDate BETWEEN :startDate AND :endDate AND p.isActive = true")
    List<Payment> findByStatusAndDateRange(@Param("statuses") List<PaymentStatus> statuses,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Analytics queries
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paymentDate >= :fromDate AND p.isActive = true")
    BigDecimal getTotalRevenueSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paymentDate >= :fromDate AND p.isActive = true")
    Long getSuccessfulPaymentCountSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT AVG(p.amount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.isActive = true")
    BigDecimal getAveragePaymentAmount();

    // Retry logic queries
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'FAILED' AND p.createdAt >= :minDate " +
            "AND NOT EXISTS (SELECT p2 FROM Payment p2 WHERE p2.subscription.id = p.subscription.id " +
            "AND p2.paymentStatus = 'COMPLETED' AND p2.createdAt > p.createdAt) AND p.isActive = true")
    List<Payment> findFailedPaymentsForRetry(@Param("minDate") LocalDateTime minDate);

    @Modifying
    @Query("DELETE FROM Payment p WHERE p.subscription.campus.id = :campusId")
    void deleteByCampusId(@Param("campusId") Long campusId);
}
