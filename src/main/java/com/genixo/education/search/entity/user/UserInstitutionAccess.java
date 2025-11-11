package com.genixo.education.search.entity.user;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.genixo.education.search.enumaration.AccessType;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_institution_access")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInstitutionAccess extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessType accessType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private LocalDateTime grantedAt;

    private LocalDateTime expiresAt;
}