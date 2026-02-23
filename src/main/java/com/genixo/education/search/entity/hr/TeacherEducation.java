package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teacher_education")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEducation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    private TeacherProfile teacherProfile;

    @Column(name = "education_level", nullable = false, length = 50)
    private String educationLevel; // Ön Lisans, Lisans, Yüksek Lisans, Doktora

    @Column(name = "institution", nullable = false, length = 200)
    private String institution;

    @Column(name = "department", length = 200)
    private String department;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
