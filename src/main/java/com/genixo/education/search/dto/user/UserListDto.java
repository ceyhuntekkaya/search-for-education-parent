package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.enumaration.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDto {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private UserType userType;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private String profileImageUrl;

    // Primary role info
    private String primaryRole;
    private RoleLevel primaryRoleLevel;

    // Institution access summary
    private Integer institutionAccessCount;
    private String primaryInstitution;
}