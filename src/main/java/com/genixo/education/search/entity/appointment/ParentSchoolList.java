package com.genixo.education.search.entity.appointment;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.ListStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parent_school_lists")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentSchoolList extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private User parentUser;

    @Column(name = "list_name", nullable = false)
    private String listName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ListStatus status = ListStatus.ACTIVE;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "color_code")
    private String colorCode; // Liste rengi için hex kod

    @Column(name = "icon")
    private String icon; // İkon adı

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "school_count")
    private Integer schoolCount = 0;

    // Relationships
    @OneToMany(mappedBy = "parentSchoolList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParentSchoolListItem> listItems = new HashSet<>();

    @OneToMany(mappedBy = "parentSchoolList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParentListNote> listNotes = new HashSet<>();
}
