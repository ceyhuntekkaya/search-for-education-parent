package com.genixo.education.search.entity.webinar;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private EventOrganizer organizer; // Düzenleyen kurum/kişi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser; // Etkinliği oluşturan kullanıcı

    @Column(name = "title", nullable = false, length = 200)
    private String title; // Etkinlik başlığı

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Açıklama

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType; // Webinar, Seminer, Eğitim, Workshop

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_format", nullable = false, length = 20)
    private DeliveryFormat deliveryFormat; // Online, Yüz yüze, Hibrit

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime; // Başlangıç tarihi ve saati

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime; // Bitiş tarihi ve saati

    @Column(name = "max_capacity")
    private Integer maxCapacity; // Maksimum katılımcı sayısı (null = sınırsız)

    @Column(name = "location", length = 300)
    private String location; // Yüz yüze etkinlik için adres

    @Column(name = "online_link", length = 500)
    private String onlineLink; // Zoom, Teams vb. link

    @Column(name = "target_audience", length = 200)
    private String targetAudience; // Hedef kitle (örn: "Matematik öğretmenleri")

    @Column(name = "speaker_name", length = 100)
    private String speakerName; // Konuşmacı/eğitmen adı

    @Column(name = "speaker_bio", columnDefinition = "TEXT")
    private String speakerBio; // Konuşmacı hakkında

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl; // Kapak görseli URL

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline; // Son kayıt tarihi

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status = EventStatus.DRAFT; // Taslak, Yayında, Tamamlandı, İptal

    @Column(name = "auto_approve_registration", nullable = false)
    private Boolean autoApproveRegistration = true; // Otomatik onay mı?

    @Column(name = "certificate_enabled", nullable = false)
    private Boolean certificateEnabled = false; // Sertifika verilecek mi?

    @Column(name = "certificate_template_url", length = 500)
    private String certificateTemplateUrl; // Sertifika şablonu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EventCategory category; // Kategori (opsiyonel)

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventRegistration> registrations = new ArrayList<>();


    // Helper method - kalan kontenjan
    public Integer getRemainingCapacity() {
        if (maxCapacity == null) {
            return null; // Sınırsız
        }
        long approvedCount = registrations.stream()
                .filter(r -> r.getStatus() == EventRegistration.RegistrationStatus.APPROVED)
                .count();
        return maxCapacity - (int) approvedCount;
    }

    // Helper method - kontenjan dolu mu?
    public boolean isCapacityFull() {
        Integer remaining = getRemainingCapacity();
        return remaining != null && remaining <= 0;
    }

    public enum EventType {
        WEBINAR,        // Webinar
        SEMINAR,        // Seminer
        TRAINING,       // Eğitim
        WORKSHOP        // Atölye/Workshop
    }

    public enum DeliveryFormat {
        ONLINE,         // Online
        IN_PERSON,      // Yüz yüze
        HYBRID          // Hibrit
    }

    public enum EventStatus {
        DRAFT,          // Taslak
        PUBLISHED,      // Yayında
        COMPLETED,      // Tamamlandı
        CANCELLED       // İptal edildi
    }
}
