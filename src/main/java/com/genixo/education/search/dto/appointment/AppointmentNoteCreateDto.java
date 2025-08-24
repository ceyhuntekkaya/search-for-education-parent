package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.NoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentNoteCreateDto {
    private Long appointmentId;
    private Long authorUserId;
    private String note;
    private NoteType noteType;
    private Boolean isPrivate;
    private Boolean isImportant;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String attachmentType;
}