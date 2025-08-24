package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.AccessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInstitutionAccessDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private AccessType accessType;
    private Long entityId;
    private String entityName; // Brand/Campus/School name
    private LocalDateTime grantedAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
}