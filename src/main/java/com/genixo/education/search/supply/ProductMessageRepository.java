package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.ProductMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMessageRepository extends JpaRepository<ProductMessage, Long> {

    Page<ProductMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);

    @Query("SELECT m FROM ProductMessage m WHERE m.conversation.id = :conversationId AND m.isRead = false")
    List<ProductMessage> findUnreadMessagesByConversationId(@Param("conversationId") Long conversationId);

    @Modifying
    @Query("UPDATE ProductMessage m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.sender.id != :userId")
    void markAllAsReadByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(m) FROM ProductMessage m WHERE m.conversation.id = :conversationId")
    Long countByConversationId(@Param("conversationId") Long conversationId);
}

