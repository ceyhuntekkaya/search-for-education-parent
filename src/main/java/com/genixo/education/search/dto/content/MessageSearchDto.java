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
public class MessageSearchDto {
    private String searchTerm;
    private Long schoolId;
    private Long assignedToUserId;
    private MessageType messageType;
    private MessagePriority priority;
    private MessageStatus status;
    private Boolean followUpRequired;
    private Boolean hasAttachments;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private String tags;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}