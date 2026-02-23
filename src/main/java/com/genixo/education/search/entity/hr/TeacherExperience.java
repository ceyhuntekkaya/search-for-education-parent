package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teacher_experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherExperience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    private TeacherProfile teacherProfile;

    @Column(name = "institution", nullable = false, length = 200)
    private String institution;

    @Column(name = "role_title", nullable = false, length = 200)
    private String roleTitle; // Görev unvanı (Sınıf Öğretmeni, Matematik Öğretmeni vb.)

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate; // null = hâlâ çalışıyor

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Görev tanımı / iş açıklaması

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
