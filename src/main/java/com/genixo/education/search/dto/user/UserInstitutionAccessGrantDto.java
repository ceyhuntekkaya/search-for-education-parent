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
public class UserInstitutionAccessGrantDto {
    private Long userId;
    private AccessType accessType;
    private Long entityId;
    private LocalDateTime expiresAt;
}