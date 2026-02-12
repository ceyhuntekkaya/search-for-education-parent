package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter; // Ön yazı / motivasyon notu

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.RECEIVED; // Başvuru durumu

    @Column(name = "is_withdrawn", nullable = false)
    private Boolean isWithdrawn = false; // Aday başvuruyu geri çekti mi?

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationNote> notes = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationDocument> documents = new ArrayList<>();


    public enum ApplicationStatus {
        RECEIVED,           // Başvuru alındı
        UNDER_REVIEW,       // İnceleniyor
        INTERVIEW_SCHEDULED,// Görüşmeye çağrıldı
        OFFER_MADE,         // Teklif yapıldı
        ACCEPTED,           // Kabul edildi
        REJECTED            // Reddedildi
    }
}