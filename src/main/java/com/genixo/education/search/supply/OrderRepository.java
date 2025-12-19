package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.Order;
import com.genixo.education.search.enumaration.OrderStatus;
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
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCompanyId(Long companyId, Pageable pageable);

    Page<Order> findBySupplierId(Long supplierId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.invoiceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.trackingNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:companyId IS NULL OR o.company.id = :companyId) AND " +
           "(:supplierId IS NULL OR o.supplier.id = :supplierId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:quotationId IS NULL OR o.quotation.id = :quotationId)")
    Page<Order> searchOrders(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") Long companyId,
            @Param("supplierId") Long supplierId,
            @Param("status") OrderStatus status,
            @Param("quotationId") Long quotationId,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM Order o WHERE o.supplier.id = :supplierId")
    Long countBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.supplier.id = :supplierId AND o.status = :status")
    Long countBySupplierIdAndStatus(@Param("supplierId") Long supplierId, @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.supplier.id = :supplierId")
    BigDecimal sumTotalAmountBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.supplier.id = :supplierId")
    BigDecimal avgTotalAmountBySupplierId(@Param("supplierId") Long supplierId);

    // Company dashboard queries
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.company.id = :companyId AND o.status = 'DELIVERED'")
    BigDecimal sumTotalAmountByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.company.id = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.company.id = :companyId AND o.status = :status")
    Long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.company.id = :companyId")
    BigDecimal avgTotalAmountByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.company.id = :companyId AND o.createdAt >= :date")
    BigDecimal sumTotalAmountByCompanyIdAndDateAfter(@Param("companyId") Long companyId, @Param("date") java.time.LocalDateTime date);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.company.id = :companyId AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    BigDecimal sumTotalAmountByCompanyIdAndDateRange(@Param("companyId") Long companyId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query(value = "SELECT AVG(DATEDIFF(o.actual_delivery_date, o.created_at)) FROM orders o WHERE o.company_id = :companyId AND o.actual_delivery_date IS NOT NULL", nativeQuery = true)
    Long avgDeliveryDaysByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.supplier.id = :supplierId AND o.createdAt >= :date")
    BigDecimal sumTotalAmountBySupplierIdAndDateAfter(@Param("supplierId") Long supplierId, @Param("date") java.time.LocalDateTime date);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.supplier.id = :supplierId AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    BigDecimal sumTotalAmountBySupplierIdAndDateRange(@Param("supplierId") Long supplierId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.supplier.id = :supplierId AND o.actualDeliveryDate <= o.expectedDeliveryDate AND o.status = 'DELIVERED'")
    Long countOnTimeDeliveriesBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT new com.genixo.education.search.dto.supply.TopSupplierDto(" +
           "o.supplier.id, o.supplier.companyName, " +
           "SUM(o.totalAmount), COUNT(o), CAST(AVG(o.totalAmount) AS java.math.BigDecimal), o.supplier.averageRating) " +
           "FROM Order o WHERE o.company.id = :companyId " +
           "GROUP BY o.supplier.id, o.supplier.companyName, o.supplier.averageRating " +
           "ORDER BY SUM(o.totalAmount) DESC")
    List<com.genixo.education.search.dto.supply.TopSupplierDto> getTopSuppliersByCompanyId(@Param("companyId") Long companyId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.supply.TopCustomerDto(" +
           "o.company.id, o.company.name, " +
           "SUM(o.totalAmount), COUNT(o), CAST(AVG(o.totalAmount) AS java.math.BigDecimal)) " +
           "FROM Order o WHERE o.supplier.id = :supplierId " +
           "GROUP BY o.company.id, o.company.name " +
           "ORDER BY SUM(o.totalAmount) DESC")
    List<com.genixo.education.search.dto.supply.TopCustomerDto> getTopCustomersBySupplierId(@Param("supplierId") Long supplierId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.supply.SpendingAnalyticsDto$SpendingByCategory(" +
           "p.category.id, p.category.name, SUM(oi.totalPrice), COUNT(DISTINCT o.id)) " +
           "FROM Order o JOIN OrderItem oi ON oi.order.id = o.id " +
           "JOIN Product p ON oi.product.id = p.id " +
           "WHERE o.company.id = :companyId " +
           "GROUP BY p.category.id, p.category.name " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<com.genixo.education.search.dto.supply.SpendingAnalyticsDto.SpendingByCategory> getSpendingByCategoryForCompany(@Param("companyId") Long companyId);

    @Query("SELECT new com.genixo.education.search.dto.supply.SalesAnalyticsDto$SalesByCategory(" +
           "p.category.id, p.category.name, SUM(oi.totalPrice), COUNT(DISTINCT o.id)) " +
           "FROM Order o JOIN OrderItem oi ON oi.order.id = o.id " +
           "JOIN Product p ON oi.product.id = p.id " +
           "WHERE o.supplier.id = :supplierId " +
           "GROUP BY p.category.id, p.category.name " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<com.genixo.education.search.dto.supply.SalesAnalyticsDto.SalesByCategory> getSalesByCategoryForSupplier(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(DISTINCT o.company.id) FROM Order o WHERE o.supplier.id = :supplierId")
    Long countDistinctCustomersBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.company.id = :companyId AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    Long countByCompanyIdAndDateRange(@Param("companyId") Long companyId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.supplier.id = :supplierId AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    Long countBySupplierIdAndDateRange(@Param("supplierId") Long supplierId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}

