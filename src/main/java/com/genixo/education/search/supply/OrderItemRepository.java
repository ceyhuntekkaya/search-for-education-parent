package com.genixo.education.search.supply;

import com.genixo.education.search.dto.supply.TopProductDto;
import com.genixo.education.search.entity.supply.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT new com.genixo.education.search.dto.supply.TopProductDto(" +
           "oi.product.id, oi.product.name, oi.product.sku, " +
           "COUNT(DISTINCT oi.order.id), CAST(SUM(oi.quantity) AS long), SUM(oi.totalPrice)) " +
           "FROM OrderItem oi JOIN Order o ON oi.order.id = o.id " +
           "WHERE o.company.id = :companyId " +
           "GROUP BY oi.product.id, oi.product.name, oi.product.sku " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<TopProductDto> getTopProductsByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.supply.TopProductDto(" +
           "oi.product.id, oi.product.name, oi.product.sku, " +
           "COUNT(DISTINCT oi.order.id), CAST(SUM(oi.quantity) AS long), SUM(oi.totalPrice)) " +
           "FROM OrderItem oi JOIN Order o ON oi.order.id = o.id " +
           "WHERE o.supplier.id = :supplierId " +
           "GROUP BY oi.product.id, oi.product.name, oi.product.sku " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<TopProductDto> getTopProductsBySupplierId(@Param("supplierId") Long supplierId, Pageable pageable);
}

