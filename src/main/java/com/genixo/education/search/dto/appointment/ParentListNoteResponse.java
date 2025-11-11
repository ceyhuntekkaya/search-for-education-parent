package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentListNoteResponse {

    private Long id;
    private Long parentSchoolListId;
    private String listName;
    private String noteTitle;
    private String noteContent;
    private Boolean isImportant;
    private LocalDateTime reminderDate;

    // Attachment info
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String attachmentType;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}