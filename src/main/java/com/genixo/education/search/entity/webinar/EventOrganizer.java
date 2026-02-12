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
@Table(name = "event_organizers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizer extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name; // Düzenleyen kurum/kişi adı

    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slug; // URL-friendly isim

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private OrganizerType type; // Düzenleyen tipi

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Kurum hakkında

    @Column(name = "logo_url", length = 500)
    private String logoUrl; // Logo

    @Column(name = "website", length = 300)
    private String website; // Web sitesi

    @Column(name = "email", length = 100)
    private String email; // İletişim e-postası

    @Column(name = "phone", length = 20)
    private String phone; // İletişim telefonu

    @Column(name = "address", columnDefinition = "TEXT")
    private String address; // Adres

    @Column(name = "city", length = 50)
    private String city; // Şehir

    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks; // JSON formatında sosyal medya linkleri

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false; // Platform tarafından onaylı mı?

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser; // Profili oluşturan kullanıcı (admin veya kurum temsilcisi)

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizerContact> contacts = new ArrayList<>();



    public enum OrganizerType {
        UNIVERSITY,             // Üniversite
        EDUCATION_COMPANY,      // Eğitim şirketi
        ASSOCIATION,            // Dernek
        GOVERNMENT,             // Devlet kurumu (MEB, vb.)
        INDIVIDUAL_TRAINER,     // Bireysel eğitmen
        PLATFORM,              // Platform kendisi (Genixo)
        OTHER                  // Diğer
    }
}