package com.genixo.education.search.repository.ai;


import com.genixo.education.search.entity.ai.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Kullanıcının aktif conversation'ını bul
    Optional<Conversation> findByUserIdAndStatusAndIsActiveTrue(
            Long userId,
            Conversation.ConversationStatus status
    );

    // Kullanıcının tüm conversation'larını getir
    List<Conversation> findByUserIdAndIsActiveTrueOrderByLastMessageAtDesc(Long userId);

    // Belirli bir tipteki conversation'ları getir
    List<Conversation> findByUserIdAndConversationTypeAndIsActiveTrueOrderByLastMessageAtDesc(
            Long userId,
            Conversation.ConversationType conversationType
    );

    // Tamamlanmış form'ları getir
    List<Conversation> findByUserIdAndIsFormSubmittedTrueAndIsActiveTrueOrderByCompletedAtDesc(Long userId);

    // Belirli bir tarihten sonra güncellenen conversation'lar
    List<Conversation> findByUserIdAndLastMessageAtAfterAndIsActiveTrue(
            Long userId,
            LocalDateTime after
    );

    // Aktif conversation sayısı
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.userId = :userId AND c.status = 'ACTIVE' AND c.isActive = true")
    Long countActiveConversationsByUserId(@Param("userId") Long userId);

    // Terk edilmiş conversation'ları bul (son 24 saatte mesaj atılmamış)
    @Query("SELECT c FROM Conversation c WHERE c.status = 'ACTIVE' AND c.lastMessageAt < :threshold AND c.isActive = true")
    List<Conversation> findAbandonedConversations(@Param("threshold") LocalDateTime threshold);
}