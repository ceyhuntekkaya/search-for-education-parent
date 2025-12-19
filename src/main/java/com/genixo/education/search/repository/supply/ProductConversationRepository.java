package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.ProductConversation;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.ProductMessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductConversationRepository extends JpaRepository<ProductConversation, Long> {

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.company.id = :companyId OR c.supplier.id IN " +
           "(SELECT s.id FROM Supplier s WHERE s.id = :supplierId)")
    Page<ProductConversation> findByCompanyIdOrSupplierId(
            @Param("companyId") Long companyId,
            @Param("supplierId") Long supplierId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.company.id = :companyId")
    Page<ProductConversation> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.supplier.id = :supplierId")
    Page<ProductConversation> findBySupplierId(@Param("supplierId") Long supplierId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.product.id = :productId")
    Page<ProductConversation> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.quotation.id = :quotationId")
    Page<ProductConversation> findByQuotationId(@Param("quotationId") Long quotationId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.order.id = :orderId")
    Page<ProductConversation> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM ProductConversation c " +
           "WHERE c.id IN (SELECT m.conversation.id FROM ProductMessage m WHERE m.sender.id = :userId) " +
           "OR c.company.id IN (SELECT u.id FROM User u WHERE u.id = :userId)")
    Page<ProductConversation> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ProductMessage m " +
           "WHERE m.conversation.id = :conversationId AND m.isRead = false " +
           "AND m.sender.id != :userId")
    Long countUnreadMessagesByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(DISTINCT c.id) FROM ProductConversation c " +
           "WHERE c.id IN (SELECT m.conversation.id FROM ProductMessage m WHERE m.sender.id != :userId AND m.isRead = false) " +
           "AND (c.company.id IN (SELECT u.id FROM User u WHERE u.id = :userId) " +
           "OR c.supplier.id IN (SELECT s.id FROM Supplier s WHERE s.id IN " +
           "(SELECT u.id FROM User u WHERE u.id = :userId)))")
    Long countUnreadConversationsByUserId(@Param("userId") Long userId);
}

