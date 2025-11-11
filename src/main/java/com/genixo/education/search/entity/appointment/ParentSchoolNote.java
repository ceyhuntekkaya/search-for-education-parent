package com.genixo.education.search.entity.appointment;


import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.enumaration.NoteCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_school_notes")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentSchoolNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_school_list_item_id")
    private ParentSchoolListItem parentSchoolListItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "note_title")
    private String noteTitle;

    @Column(name = "note_content", columnDefinition = "TEXT", nullable = false)
    private String noteContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NoteCategory category = NoteCategory.GENERAL;

    @Column(name = "is_important")
    private Boolean isImportant = false;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

    @Column(name = "source")
    private String source; // Telefon, ziyaret, web sitesi vb.

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