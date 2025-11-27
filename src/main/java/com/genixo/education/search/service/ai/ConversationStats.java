package com.genixo.education.search.service.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationStats {
    private Long conversationId;
    private Long totalMessages;
    private Long totalTokens;
    private Double averageProcessingTimeMs;
}
