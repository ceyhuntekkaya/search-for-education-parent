package com.genixo.education.search.entity.webinar;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "organizer_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerContact extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private EventOrganizer organizer;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // İlişkili kullanıcı hesabı

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "title", length = 100)
    private String title; // Ünvan (örn: "Eğitim Koordinatörü")

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false; // Ana yetkili mi?

    @Column(name = "can_create_events", nullable = false)
    private Boolean canCreateEvents = true; // Etkinlik oluşturabilir mi?


}
