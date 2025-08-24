package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.RoleLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private RoleLevel roleLevel;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Permissions
    private List<PermissionDto> permissions;
}