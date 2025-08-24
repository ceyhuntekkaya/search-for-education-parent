package com.genixo.education.search.dto.user;

import com.genixo.education.search.enumaration.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserType userType;
    private String profileImageUrl;
    private Boolean isActive;
}