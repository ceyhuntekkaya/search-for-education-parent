package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.PermissionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDto {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private PermissionCategory category;
    private Boolean isActive;
    private LocalDateTime createdAt;
}