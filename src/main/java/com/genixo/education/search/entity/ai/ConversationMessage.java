package com.genixo.education.search.entity.ai;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "conversation_messages")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConversationMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role; // USER, ASSISTANT, SYSTEM

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "extracted_data", columnDefinition = "TEXT")
    private String extractedData; // AI'ın bu mesajdan çıkardığı veri (JSON)

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs; // AI yanıt süresi

    // Enum
    public enum MessageRole {
        USER,      // Kullanıcı mesajı
        ASSISTANT, // AI yanıtı
        SYSTEM     // System prompt
    }
}