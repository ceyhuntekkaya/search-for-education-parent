package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.AccessType;
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
public class UserSearchDto {
    private String searchTerm; // name, email, phone
    private UserType userType;
    private Boolean isActive;
    private Long roleId;
    private RoleLevel roleLevel;
    private Long institutionId; // Brand/Campus/School
    private AccessType accessType;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime lastLoginAfter;
    private LocalDateTime lastLoginBefore;

    // Location filters
    private Long countryId;
    private Long provinceId;
    private Long districtId;
    private Long neighborhoodId;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}