package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageStatus;
import com.genixo.education.search.enumaration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSummaryDto {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String subject;
    private MessageType messageType;
    private MessagePriority priority;
    private MessageStatus status;
    private String referenceNumber;
    private Boolean hasAttachments;
    private Boolean followUpRequired;
    private String schoolName;
    private String assignedToUserName;
    private LocalDateTime createdAt;
}