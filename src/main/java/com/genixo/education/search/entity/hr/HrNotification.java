package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Bildirimi alan kullanıcı

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application; // İlişkili başvuru (opsiyonel)

    @Column(name = "title", nullable = false, length = 200)
    private String title; // Bildirim başlığı

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message; // Bildirim içeriği

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type; // Bildirim tipi

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    public enum NotificationType {
        APPLICATION_RECEIVED,       // Yeni başvuru alındı (okul için)
        STATUS_UPDATED,            // Başvuru durumu güncellendi (öğretmen için)
        NEW_JOB_POSTED,           // Yeni ilan yayınlandı
        APPLICATION_WITHDRAWN,      // Başvuru geri çekildi
        EVENT_REGISTRATION_RECEIVED,    // Yeni katılım başvurusu (düzenleyen için)
        EVENT_REGISTRATION_APPROVED,    // Kayıt onaylandı (katılımcı için)
        EVENT_REGISTRATION_REJECTED,    // Kayıt reddedildi (katılımcı için)
        EVENT_REMINDER_24H,            // 24 saat öncesi hatırlatma
        EVENT_REMINDER_1H,             // 1 saat öncesi hatırlatma
        EVENT_CANCELLED,               // Etkinlik iptal edildi
        EVENT_UPDATED,                 // Etkinlik güncellendi
        CERTIFICATE_READY
    }
}
