package com.genixo.education.search.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private Long conversationId; // null ise yeni conversation

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;

    @NotNull(message = "User ID zorunludur")
    private Long userId;
}