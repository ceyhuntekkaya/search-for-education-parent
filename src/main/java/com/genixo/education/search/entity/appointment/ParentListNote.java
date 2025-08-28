package com.genixo.education.search.entity.appointment;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_list_notes")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentListNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_school_list_id", nullable = false)
    private ParentSchoolList parentSchoolList;

    @Column(name = "note_title")
    private String noteTitle;

    @Column(name = "note_content", columnDefinition = "TEXT", nullable = false)
    private String noteContent;

    @Column(name = "is_important")
    private Boolean isImportant = false;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

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