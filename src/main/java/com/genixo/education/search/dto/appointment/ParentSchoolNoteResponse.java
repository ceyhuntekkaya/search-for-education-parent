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
public class ParentSchoolNoteResponse {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long parentSchoolListItemId;
    private String noteTitle;
    private String noteContent;
    private String category;
    private Boolean isImportant;
    private LocalDateTime reminderDate;
    private String source;

    // Attachment info
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String attachmentType;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
