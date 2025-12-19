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
public class NotificationDto {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String notificationType;
    private Boolean isRead;
    private Long referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

