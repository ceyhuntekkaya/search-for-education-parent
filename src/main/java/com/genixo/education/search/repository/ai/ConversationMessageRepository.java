package com.genixo.education.search.repository.ai;

import com.genixo.education.search.entity.ai.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {

    // Conversation'a ait tüm mesajları getir (sıralı)
    List<ConversationMessage> findByConversationIdAndIsActiveTrueOrderByCreatedAtAsc(Long conversationId);

    // Son N mesajı getir (context için)
    @Query("SELECT m FROM ConversationMessage m WHERE m.conversation.id = :conversationId AND m.isActive = true ORDER BY m.createdAt DESC LIMIT :limit")
    List<ConversationMessage> findLastNMessages(@Param("conversationId") Long conversationId, @Param("limit") int limit);

    // Extracted data olan mesajları getir
    @Query("SELECT m FROM ConversationMessage m WHERE m.conversation.id = :conversationId AND m.extractedData IS NOT NULL AND m.isActive = true ORDER BY m.createdAt ASC")
    List<ConversationMessage> findMessagesWithExtractedData(@Param("conversationId") Long conversationId);

    // Toplam token sayısını hesapla
    @Query("SELECT COALESCE(SUM(m.tokenCount), 0) FROM ConversationMessage m WHERE m.conversation.id = :conversationId AND m.isActive = true")
    Long getTotalTokenCount(@Param("conversationId") Long conversationId);

    // Ortalama yanıt süresini hesapla
    @Query("SELECT AVG(m.processingTimeMs) FROM ConversationMessage m WHERE m.conversation.id = :conversationId AND m.role = 'ASSISTANT' AND m.isActive = true")
    Double getAverageProcessingTime(@Param("conversationId") Long conversationId);

    // Mesaj sayısı
    Long countByConversationIdAndIsActiveTrue(Long conversationId);

    // Role göre mesajları getir
    List<ConversationMessage> findByConversationIdAndRoleAndIsActiveTrueOrderByCreatedAtAsc(
            Long conversationId,
            ConversationMessage.MessageRole role
    );
}