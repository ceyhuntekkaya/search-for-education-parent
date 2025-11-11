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
public class UserRegistrationDto {
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private UserType userType;

    // Location (optional for parents)
    private Long countryId;
    private Long provinceId;
    private Long districtId;
    private Long neighborhoodId;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;

    // Terms and privacy
    private Boolean acceptTerms;
    private Boolean acceptPrivacy;
    private Boolean acceptMarketing;
}