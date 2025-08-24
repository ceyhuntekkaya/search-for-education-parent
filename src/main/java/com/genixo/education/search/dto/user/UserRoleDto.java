package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.RoleLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long roleId;
    private String roleName;
    private String roleDisplayName;
    private RoleLevel roleLevel;
    private LocalDateTime grantedAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
}