package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.location.Province;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPosting extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private Campus campus;

    @Column(name = "position_title", nullable = false, length = 200)
    private String positionTitle; // Pozisyon adı

    @Column(name = "branch", nullable = false, length = 100)
    private String branch; // Branş (Matematik, İngilizce vb.)

    @Column(name = "employment_type", length = 50)
    private String employmentType; // Tam zamanlı, Yarı zamanlı, Vekil, Ders saati bazlı

    @Column(name = "start_date")
    private LocalDate startDate; // Başlangıç tarihi

    @Column(name = "contract_duration", length = 100)
    private String contractDuration; // Süre (1 yıl, süresiz, vb.)

    @Column(name = "required_experience_years")
    private Integer requiredExperienceYears; // Gerekli deneyim (yıl)

    @Column(name = "required_education_level", length = 50)
    private String requiredEducationLevel; // Gerekli eğitim seviyesi

    @Column(name = "salary_min")
    private Integer salaryMin; // Ücret alt sınırı (opsiyonel)

    @Column(name = "salary_max")
    private Integer salaryMax; // Ücret üst sınırı (opsiyonel)

    @Column(name = "show_salary", nullable = false)
    private Boolean showSalary = false; // Ücret gösterilsin mi?

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // İlan açıklaması

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline; // Son başvuru tarihi

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobPostingStatus status = JobPostingStatus.DRAFT; // Taslak, Yayında, Kapalı, Tamamlandı

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true; // Herkese açık mı, sadece kayıtlı kullanıcılara mı?

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_posting_provinces",
            joinColumns = @JoinColumn(name = "job_posting_id"),
            inverseJoinColumns = @JoinColumn(name = "province_id")
    )
    private List<Province> provinces = new ArrayList<>();

    public enum JobPostingStatus {
        DRAFT,          // Taslak
        PUBLISHED,      // Yayında
        CLOSED,         // Başvuru kapalı
        COMPLETED       // Tamamlandı
    }
}