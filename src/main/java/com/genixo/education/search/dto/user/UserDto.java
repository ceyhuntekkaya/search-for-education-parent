package com.genixo.education.search.dto.user;

import com.genixo.education.search.entity.user.Department;
import com.genixo.education.search.entity.user.Permission;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String fullName; // firstName + lastName
    private UserType userType;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private LocalDateTime lastLoginAt;
    private String profileImageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Location information
    private String country;
    private String province;
    private String district;
    private String neighborhood;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    // Relationships
    private List<UserRole> userRoles;
    private List<UserInstitutionAccessDto> institutionAccess;
}