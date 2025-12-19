package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private String content;
    private String attachmentUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

