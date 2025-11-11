package com.genixo.education.search.dto.user;

import com.genixo.education.search.dto.institution.SchoolDto;
import com.genixo.education.search.dto.subscription.SubscriptionDto;
import com.genixo.education.search.entity.subscription.Subscription;
import com.genixo.education.search.entity.user.Department;
import com.genixo.education.search.entity.user.Permission;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.enumaration.RoleLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDto {
    private Long id;
    private Long userId;
    private Role role; // Role ENUM
    private Set<Department> departments; // Department ENUM
    private Set<Permission> permissions; // Permission ENUM
    private RoleLevel roleLevel; // RoleLevel ENUM
    private LocalDateTime expiresAt;
    private SubscriptionDto subscription;
    private Set<SchoolDto> schools;
}
