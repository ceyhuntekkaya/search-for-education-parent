package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageUpdateDto {
    private MessageStatus status;
    private MessagePriority priority;
    private Long assignedToUserId;
    private String internalNotes;
    private String tags;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private String followUpNotes;
}
