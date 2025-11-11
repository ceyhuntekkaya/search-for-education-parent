package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.NoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentNoteDto {
    private Long id;
    private Long appointmentId;
    private Long authorUserId;
    private String authorUserName;
    private String note;
    private NoteType noteType;
    private Boolean isPrivate;
    private Boolean isImportant;
    private LocalDateTime noteDate;

    // File attachments
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String attachmentType;

    // Calculated fields
    private String noteTypeDisplayName;
    private String formattedNoteDate;
    private Boolean canEdit;
    private Boolean canDelete;
}
