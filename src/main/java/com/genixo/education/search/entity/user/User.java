package com.genixo.education.search.entity.user;


import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.entity.location.Country;
import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Neighborhood;
import com.genixo.education.search.entity.location.Province;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    private Boolean isEmailVerified = false;

    private Boolean isPhoneVerified = false;

    private String emailVerificationToken;

    private String phoneVerificationCode;

    private String passwordResetToken;

    private LocalDateTime passwordResetExpiresAt;

    private LocalDateTime lastLoginAt;

    private String profileImageUrl;

    // Location information (for parents to find nearby schools)
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    private Neighborhood neighborhood;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private Double latitude;

    private Double longitude;


    // User institution access
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserInstitutionAccess> institutionAccess = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        assert o instanceof User;
        User that = (User) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Rolleri ekle
        if (userRoles != null) {
            for (UserRole userRole : userRoles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().name()));

                // Permission'ları da ekle
                if (userRole.getRole().getPermissions() != null) {
                    for (Permission permission : userRole.getRole().getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(permission.name()));
                    }
                }
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
