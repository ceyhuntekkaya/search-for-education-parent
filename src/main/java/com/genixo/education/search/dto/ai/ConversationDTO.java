package com.genixo.education.search.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private Long userId;
    private String conversationType;
    private String status;
    private FormDataDTO currentFormData;
    private Boolean isFormSubmitted;
    private LocalDateTime lastMessageAt;
    private LocalDateTime completedAt;
    private Integer messageCount;
    private List<ConversationMessageDTO> recentMessages; // Son 5-10 mesaj
}