package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.RoleLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCreateDto {
    private String name;
    private String displayName;
    private String description;
    private RoleLevel roleLevel;
    private List<Long> permissionIds;
}