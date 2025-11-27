package com.genixo.education.search.entity.ai;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Conversation extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    private ConversationType conversationType; // OKUL_FORMU, GENEL, vb.

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status = ConversationStatus.ACTIVE; // ACTIVE, COMPLETED, ABANDONED

    @Column(name = "form_data", columnDefinition = "TEXT")
    private String formData; // JSON olarak parse edilmiş form verisi

    @Column(name = "is_form_submitted")
    private Boolean isFormSubmitted = false;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Enum types
    public enum ConversationType {
        OKUL_FORMU,
        GENEL,
        DESTEK
    }

    public enum ConversationStatus {
        ACTIVE,      // Devam ediyor
        COMPLETED,   // Form gönderildi
        ABANDONED    // Yarıda bırakıldı
    }
}