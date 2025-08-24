package com.genixo.education.search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleAssignDto {
    private Long userId;
    private Long roleId;
    private LocalDateTime expiresAt;
}