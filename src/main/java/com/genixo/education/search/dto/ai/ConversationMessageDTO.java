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
public class ConversationMessageDTO {
    private Long id;
    private Long conversationId;
    private String role;
    private String content;
    private FormDataDTO extractedData;
    private LocalDateTime createdAt;
    private Long processingTimeMs;
}