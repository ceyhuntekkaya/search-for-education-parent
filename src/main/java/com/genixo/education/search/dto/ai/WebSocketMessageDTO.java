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
public class WebSocketMessageDTO {
    private String type; // CHAT_MESSAGE, FORM_UPDATE, TYPING, ERROR
    private Long conversationId;
    private ChatMessageResponse chatMessage; // type=CHAT_MESSAGE için
    private FormDataDTO formUpdate; // type=FORM_UPDATE için
    private String errorMessage; // type=ERROR için
    private LocalDateTime timestamp;
}