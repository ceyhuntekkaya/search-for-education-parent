package com.genixo.education.search.entity.appointment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.enumaration.SchoolItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parent_school_list_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"parent_school_list_id", "school_id"}))
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentSchoolListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_school_list_id", nullable = false)
    private ParentSchoolList parentSchoolList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "star_rating")
    private Integer starRating; // 1-5 yıldız

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SchoolItemStatus status = SchoolItemStatus.ACTIVE;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "priority_order")
    private Integer priorityOrder = 0; // Liste içinde sıralama

    @Column(name = "personal_notes", columnDefinition = "TEXT")
    private String personalNotes;

    @Column(name = "pros", columnDefinition = "TEXT")
    private String pros; // Artılar

    @Column(name = "cons", columnDefinition = "TEXT")
    private String cons; // Eksiler

    @Column(name = "tags")
    private String tags; // Virgülle ayrılmış etiketler

    @Column(name = "visit_planned_date")
    private LocalDateTime visitPlannedDate;

    @Column(name = "visit_completed_date")
    private LocalDateTime visitCompletedDate;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "added_from_search")
    private String addedFromSearch; // Hangi aramadan eklendiği

    // Relationships
    @OneToMany(mappedBy = "parentSchoolListItem", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<ParentSchoolNote> schoolNotes = new HashSet<>();
}