package com.genixo.education.search.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long conversationId;
    private Long messageId;
    private String role; // USER, ASSISTANT, SYSTEM
    private String content;
    private FormDataDTO extractedFormData; // AI'ın çıkardığı form verisi
    private LocalDateTime timestamp;
    private Long processingTimeMs;
    private Boolean isFormComplete; // Form tamamlandı mı?
}