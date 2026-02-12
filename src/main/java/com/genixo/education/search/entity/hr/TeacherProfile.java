package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teacher_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "branch", length = 100)
    private String branch; // Branş/ders alanı (örn: Matematik, İngilizce)

    @Column(name = "education_level", length = 50)
    private String educationLevel; // Lisans, Yüksek Lisans, Doktora

    @Column(name = "experience_years")
    private Integer experienceYears; // Deneyim süresi (yıl)

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio; // Kısa tanıtım metni

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl; // Profil fotoğrafı

    @Column(name = "video_url", length = 500)
    private String videoUrl; // Tanıtım videusu linki

    @Column(name = "cv_url", length = 500)
    private String cvUrl; // CV dosyası URL'i

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_profile_provinces",
            joinColumns = @JoinColumn(name = "teacher_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "province_id")
    )
    private List<Province> provinces = new ArrayList<>();
}