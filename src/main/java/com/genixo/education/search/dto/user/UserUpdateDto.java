package com.genixo.education.search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImageUrl;

    // Location
    private Long countryId;
    private Long provinceId;
    private Long districtId;
    private Long neighborhoodId;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    // Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private String preferredLanguage;
    private String timezone;
}
