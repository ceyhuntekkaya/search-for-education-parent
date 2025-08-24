package com.genixo.education.search.entity.appointment;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.NoteType;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_notes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User authorUser;

    @Column(name = "note", columnDefinition = "TEXT", nullable = false)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type")
    private NoteType noteType = NoteType.GENERAL;

    @Column(name = "is_private")
    private Boolean isPrivate = false; // Sadece okul personeli görebilir

    @Column(name = "is_important")
    private Boolean isImportant = false;

    @Column(name = "note_date", nullable = false)
    private LocalDateTime noteDate;

    // File attachments
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "attachment_type")
    private String attachmentType;
}