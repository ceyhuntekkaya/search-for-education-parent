package com.genixo.education.search.entity.webinar;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "teacher_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(name = "registration_note", columnDefinition = "TEXT")
    private String registrationNote; // Katılımcının eklediği not (opsiyonel)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RegistrationStatus status = RegistrationStatus.PENDING; // Kayıt durumu

    @Column(name = "attended", nullable = false)
    private Boolean attended = false; // Katıldı mı?

    @Column(name = "attendance_marked_at")
    private LocalDateTime attendanceMarkedAt; // Katılım ne zaman işaretlendi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_marked_by_user_id")
    private User attendanceMarkedBy; // Katılımı kim işaretledi

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl; // Sertifika URL'i

    @Column(name = "certificate_generated_at")
    private LocalDateTime certificateGeneratedAt;

    public enum RegistrationStatus {
        PENDING,        // Onay bekliyor
        APPROVED,       // Onaylandı
        REJECTED,       // Reddedildi
        CANCELLED       // İptal edildi (katılımcı iptal etti)
    }
}