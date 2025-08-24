package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.PermissionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionCreateDto {
    private String name;
    private String displayName;
    private String description;
    private PermissionCategory category;
}