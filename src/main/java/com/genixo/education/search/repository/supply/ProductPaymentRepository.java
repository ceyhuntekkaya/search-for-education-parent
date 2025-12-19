package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.ProductPayment;
import com.genixo.education.search.enumaration.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPaymentRepository extends JpaRepository<ProductPayment, Long> {

    Optional<ProductPayment> findByOrderId(Long orderId);

    @Query("SELECT p FROM ProductPayment p WHERE p.order.company.id = :companyId")
    Page<ProductPayment> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM ProductPayment p WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:companyId IS NULL OR p.order.company.id = :companyId) AND " +
           "(:orderId IS NULL OR p.order.id = :orderId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<ProductPayment> searchPayments(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") Long companyId,
            @Param("orderId") Long orderId,
            @Param("status") PaymentStatus status,
            Pageable pageable
    );
}

